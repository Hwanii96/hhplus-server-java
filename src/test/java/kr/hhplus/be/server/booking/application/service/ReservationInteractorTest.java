package kr.hhplus.be.server.booking.application.service;

import kr.hhplus.be.server.booking.application.command.ReservationCommand;
import kr.hhplus.be.server.booking.application.result.ReservationResult;
import kr.hhplus.be.server.booking.domain.model.entity.Reservation;
import kr.hhplus.be.server.booking.domain.policy.SeatHoldPolicy;
import kr.hhplus.be.server.booking.port.outbound.QueueTokenPort;
import kr.hhplus.be.server.booking.port.outbound.ReservationPort;
import kr.hhplus.be.server.booking.port.outbound.SeatAvailabilityPort;
import kr.hhplus.be.server.booking.port.outbound.SeatLockPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * 예약을 통해 좌석을 임시 배정 받는다 -> 백엔드가 받아야할 입력 -> concertId, scheduleId, seatId, userId, queueToken
 *
 * [ 정책 ]
 * 1. 좌석 예약을 성공적으로 수행하기 위해서는 queueToken (대기열 토큰) 의 값이 ACTIVE 여야 한다
 * 2. 좌석 예약 시 동시성 문제가 해결되어야 한다
 * 3. 좌석 예약이 성공적으로 수행되면 해당 좌석의 상태 값이 AVAILABLE에서 TEMPORARY로 변경되어야 한다
 * 4. 좌석 예약이 성공적으로 수행되면 좌석 임시 배정 (예약) 만료 시각을 현재 시각 (now) + seatHoldPolicy (Duration) 으로 세팅되어야 하며, 해당 좌석은 다른 사용자로부터 접근이 되어서는 안된다
 */

@ExtendWith(MockitoExtension.class)
class ReservationInteractorTest {

    @Mock
    QueueTokenPort queueTokenPort; // 예 : Redis
    @Mock
    ReservationPort reservationPort; // 예 : DB
    @Mock
    SeatLockPort seatLockPort; // 예 : Redis
    @Mock
    SeatAvailabilityPort seatAvailabilityPort; // 예 : DB

    private ReservationInteractor reservationInteractor;

    private final SeatHoldPolicy seatHoldPolicy = new SeatHoldPolicy(Duration.ofMinutes(5));
    private final Instant now = Instant.parse("2025-12-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        reservationInteractor = new ReservationInteractor
                (
                queueTokenPort,
                seatAvailabilityPort,
                seatLockPort,
                reservationPort,
                seatHoldPolicy,
                () -> now
                );
    }

    /**
     * 좌석 예약 시 대기열 토큰 상태를 조회해서 ACTIVE 상태가 아닌 경우 예약이 불가능한 상황을 하나의 red 단위 코드로 작성
     * 무조건 좌석 예약 시 대기열 토큰 상태가 ACTIVE 여야만 가능하므로, 해당 테스트 코드를 우선적으로 작성했음
     */
    @Test
    void seats_reservation_fails_when_queueToken_is_not_active_status() {

        // given
        String token = "testToken";
        long userId = 1L;
        long scheduleId = 10L;
        long seatId = 100L;

        when(queueTokenPort.isActive(token, userId, scheduleId)).thenReturn(false);

        ReservationCommand reservationCommand = new ReservationCommand(token, userId, scheduleId, seatId);

        // when & then
        // assertThrows() : "코드 실행 시 특정 예외가 반드시 발생해야 한다" 를 검증하는 assert 함수
        assertThrows(IllegalStateException.class, () -> reservationInteractor.reserve(reservationCommand));

        // then
        verifyNoInteractions(seatLockPort);
        verifyNoInteractions(reservationPort);

    }

    /**
     * 좌석 예약 시 대기열 토큰 상태를 조회해서 ACTIVE 상태인 경우 예약이 가능한 기본적인 조건을 충족하게 된다
     * 아래의 테스트 메서드는 좌석 예약 시 동시성 문제 (좌석 예약 시 선점 기능) 를 제어하기 위한 red 단위이다
     */
    @Test
    void seats_reservation_fails_when_seatLock_hold_is_failed() {

        // given
        String token = "testToken";
        long userId = 2L;
        long scheduleId = 20L;
        long seatId = 200L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);

        when(queueTokenPort.isActive(token, userId, scheduleId)).thenReturn(true);
        when(seatLockPort.hold(userId, scheduleId, seatId, expiresAt)).thenReturn(false);

        ReservationCommand reservationCommand = new ReservationCommand(token, userId, scheduleId, seatId);

        // when
        assertThrows(IllegalStateException.class, () -> reservationInteractor.reserve(reservationCommand));

        // then : hold() 호출은 했어야 한다
        verify(seatLockPort).hold(userId, scheduleId, seatId, expiresAt);

        // then : hold() 호출 이후 실패 시 예약이 진행되면 안된다
        verifyNoInteractions(reservationPort);
        
    }

    /**
     * 좌석 예약 시 대기열 토큰 상태를 조회해서 ACTIVE 상태인 경우 예약이 가능한 기본적인 조건을 충족하게 된다
     * 아래의 테스트 메서드는 좌석 예약 시 동시성 문제가 해결된 후 최종적으로 좌석 임시 배정이 가능한지를 테스트하기 위한 red 단위이다
     * reserve() 의 return 값을 미구현한 단계까지를 기준으로 테스트 작성
     */
    @Test
    void seats_reservation_success_when_seatLock_hold_is_succeed() {

        // given
        String token = "testToken";
        long userId = 3L;
        long scheduleId = 30L;
        long seatId = 300L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);

        when(queueTokenPort.isActive(token, userId, scheduleId)).thenReturn(true);
        when(seatLockPort.hold(userId, scheduleId, seatId, expiresAt)).thenReturn(true);

        ReservationCommand reservationCommand = new ReservationCommand(token, userId, scheduleId, seatId);

        // when : interactor에서 reserve() 성공 시 result가 아직 미구현 단계이므로, UnsupportedOperationException으로 임시 처리
        assertThrows(UnsupportedOperationException.class, () -> reservationInteractor.reserve(reservationCommand));

        // then
        // any : Mockito의 Argument Matcher이며, 인자의 내용을 검증하지 않고 테스트를 수행하려고 할 때 (호출 여부만 빠르게 검증) 사용할 수 있으며, any() 인자로 타입을 명시하면 더 안전하다
        verify(reservationPort).reserve(any(Reservation.class));

    }

    /**
     * 좌석 예약 시 대기열 토큰 상태를 조회해서 ACTIVE 상태인 경우 예약이 가능한 기본적인 조건을 충족하게 된다
     * 아래의 테스트 메서드는 좌석 예약 시 동시성 문제가 해결된 후 최종적으로 좌석 임시 배정이 가능한지를 테스트하기 위한 red 단위이다
     * reserve() 의 return 값을 구현한 단계까지를 기준으로 해서 최종적으로 마무리하기 위한 테스트
     */
    @Test
    void seats_reservation_success_returns_results_when_seatLock_hold_is_succeed() {

        // given
        String token = "testToken";
        long userId = 4L;
        long scheduleId = 40L;
        long seatId = 400L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);

        when(queueTokenPort.isActive(token, userId, scheduleId)).thenReturn(true);
        when(seatLockPort.hold(userId, scheduleId, seatId, expiresAt)).thenReturn(true);

        // temporaryReservation() 규칙 상 내부에서 id 값은 null로 생성되도록 했다
        Reservation temporaryReservation = Reservation.temporaryReservation(userId, scheduleId, seatId, expiresAt);

        // 따라서 테스트를 위해 id 값을 DB에서 받았다고 가정해야 하므로 불변 필드를 withId() 메서드로 변경 (새롭게 Reservation 객체 생성) 하도록 한다
        Reservation temporaryReservationWithId = temporaryReservation.withId(1L);

        when(reservationPort.reserve(any(Reservation.class))).thenReturn(temporaryReservationWithId);

        ReservationCommand reservationCommand = new ReservationCommand(token, userId, scheduleId, seatId);

        // when
        ReservationResult reservationResult = reservationInteractor.reserve(reservationCommand);

        // then
        assertEquals(1L, reservationResult.reservationId());
        assertEquals(expiresAt, reservationResult.reservationExpiresAt());

        verify(reservationPort).reserve(any(Reservation.class));
        
    }

    /**
     * 좌석 예약 (임시 배정) 시 seatId 값을 통해 조회해서 해당 좌석의 상태 값이 이미 RESERVED인 경우에는 예약이 불가능한지를 검증하기 위한 red 단위
     *
     */
    @Test
    void seats_reservation_fails_when_seat_is_not_available() {

        // given
        String token = "testToken";
        long userId = 5L;
        long scheduleId = 50L;
        long seatId = 500L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);

        when(queueTokenPort.isActive(token, userId, scheduleId)).thenReturn(true);
        when(seatAvailabilityPort.isAvailable(scheduleId, seatId)).thenReturn(false);

        ReservationCommand reservationCommand = new ReservationCommand(token, userId, scheduleId, seatId);

        // when
        assertThrows(IllegalStateException.class, () -> reservationInteractor.reserve(reservationCommand));

        // then : isAvailable() 호출은 했어야 한다
        verify(seatAvailabilityPort).isAvailable(scheduleId, seatId);

        // then : isAvailable() 결과로 좌석 예약 (임시 배정) 이 불가능한 경우 아래의 Port 들은 호출되면 안된다
        verifyNoInteractions(seatLockPort);
        verifyNoInteractions(reservationPort);

    }
    
    /*
      [ 임시 메모 ]
      좌석 예약 시 대기열 통과한 후 특정 좌석을 임시 배정 받고 포인트로 결제하여 예매를 완료하는 시나리오를 테스트하기 위한 메서드
      아래의 메서드에서는 좌석 임시 배정 (예약) 까지만 테스트를 진행하도록 하며, 포인트 결제 및 예매 테스트는 PaymentInteractorTest 클래스에서 수행하도록 함
      대기열 토큰 발급은 별도로 QueueTokenServiceTest에서 진행하도록 함
      대기열 토큰 조회는 ReservationInteractorTest에서 QueueTokenPort를 Mock으로 대체해서 진행하도록 한다
      좌석 상태 (AVAILABLE, TEMPORARY, RESERVED) 는 화면에서 데이터가 존재한다고 가정하고 테스트를 수행할 것이므로 별도의 테스트를 이곳에서 수행하지 않음
     */

}
