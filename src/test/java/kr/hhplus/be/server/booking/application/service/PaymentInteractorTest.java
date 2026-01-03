package kr.hhplus.be.server.booking.application.service;

import kr.hhplus.be.server.booking.application.command.PaymentCommand;
import kr.hhplus.be.server.booking.domain.model.entity.Reservation;
import kr.hhplus.be.server.booking.domain.policy.SeatHoldPolicy;
import kr.hhplus.be.server.booking.port.outbound.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentInteractorTest {

    @Mock
    QueueTokenPort queueTokenPort;
    @Mock
    SeatPort seatPort;
    @Mock
    ReservationPort reservationPort;
    @Mock
    PointPort pointPort;
    @Mock
    PaymentRepositoryPort paymentRepositoryPort;

    private PaymentInteractor paymentInteractor;

    private final SeatHoldPolicy seatHoldPolicy = new SeatHoldPolicy(Duration.ofMinutes(5));
    private final Instant now = Instant.parse("2026-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        paymentInteractor = new PaymentInteractor
                (
                        queueTokenPort,
                        seatPort,
                        reservationPort,
                        pointPort,
                        paymentRepositoryPort,
                        () -> now
                );
    }

    /**
     * 해당하는 예약 정보를 찾을 수 없는 경우 테스트
     */
    @Test
    void payment_fails_when_reservation_not_found() {

        // given
        String token = "testToken";
        long userId = 1L;
        long reservationId = 10L;

        when(reservationPort.searchByReservationId(reservationId)).thenReturn(null);

        PaymentCommand paymentCommand = new PaymentCommand(token, userId, reservationId);

        // when
        assertThrows(IllegalStateException.class, () -> paymentInteractor.pay(paymentCommand));

        // then
        verify(reservationPort).searchByReservationId(reservationId);

        // 결제 시 예약 정보가 유효한지를 먼저 체크하지 않고 대기열 토큰 상태가 "ACTIVE" 인지를 우선적으로 확인하도록 할 수도 있지만, 대기열 토큰 검증 시 (isActive(token, userId, scheduleId)) scheduleId가 필요하므로 대기열 토큰 상태를 검증하는 것을 예약 정보 유효성 체크 후 진행하도록 함
        verifyNoInteractions(queueTokenPort);
        verifyNoInteractions(seatPort, pointPort, paymentRepositoryPort);

    }

    @Test
    void payment_fails_when_queueToken_is_not_active_status() {

        // given
        long userId = 2L;
        long scheduleId = 20L;
        long seatId = 200L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);
        long reservationId = 2000L;
        String token = "testToken";

        Reservation temporaryReservation =  Reservation.temporaryReservation(userId, scheduleId,seatId, expiresAt);
        Reservation temporaryReservationWithId = temporaryReservation.withId(reservationId);

        when(reservationPort.searchByReservationId(reservationId)).thenReturn(temporaryReservationWithId);
        when(queueTokenPort.isActive(token, userId, temporaryReservationWithId.getScheduleId())).thenReturn(false);

        PaymentCommand paymentCommand = new PaymentCommand(token, userId, reservationId);

        //
        assertThrows(IllegalStateException.class, () -> paymentInteractor.pay(paymentCommand));

        // then
        verify(queueTokenPort).isActive(token, userId, temporaryReservationWithId.getScheduleId());
        verifyNoInteractions(seatPort, pointPort, paymentRepositoryPort);


        // then

    }






}
