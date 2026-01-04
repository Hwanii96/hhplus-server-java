package kr.hhplus.be.server.booking.application.service;

import kr.hhplus.be.server.booking.application.command.PaymentCommand;
import kr.hhplus.be.server.booking.application.result.PaymentResult;
import kr.hhplus.be.server.booking.domain.model.entity.Payment;
import kr.hhplus.be.server.booking.domain.model.entity.Point;
import kr.hhplus.be.server.booking.domain.model.entity.Reservation;
import kr.hhplus.be.server.booking.domain.model.enums.ReservationStatus;
import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.booking.domain.policy.SeatHoldPolicy;
import kr.hhplus.be.server.booking.port.outbound.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    /**
     *
     */
    @Test
    void payment_fails_when_queueToken_is_not_active_status() {

        // given
        long userId = 2L;
        long scheduleId = 20L;
        long seatId = 200L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);
        long reservationId = 2000L;
        String token = "testToken";

        Reservation temporaryReservation =  Reservation.temporaryReservation(userId, scheduleId, seatId, expiresAt);
        Reservation temporaryReservationWithId = temporaryReservation.withId(reservationId);

        when(reservationPort.searchByReservationId(reservationId)).thenReturn(temporaryReservationWithId);
        when(queueTokenPort.isActive(token, userId, temporaryReservationWithId.getScheduleId())).thenReturn(false);

        PaymentCommand paymentCommand = new PaymentCommand(token, userId, reservationId);

        //
        assertThrows(IllegalStateException.class, () -> paymentInteractor.pay(paymentCommand));

        // then
        verify(reservationPort).searchByReservationId(reservationId);
        verify(queueTokenPort).isActive(token, userId, temporaryReservationWithId.getScheduleId());
        verifyNoInteractions(seatPort, pointPort, paymentRepositoryPort);

    }

    /**
     *
     */
    @Test
    void payment_fails_when_reservation_owner_mismatch() {

        // given
        long userId = 3L;
        long scheduleId = 30L;
        long seatId = 300L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);
        long reservationId = 3000L;
        String token = "testToken";
        
        // 테스트를 위한 특정 예약 건의 소유자 id 값을 세팅
        long ownerUserId = 777L;

        // when
        Reservation temporaryReservation = Reservation.temporaryReservation(ownerUserId, scheduleId, seatId, expiresAt);
        Reservation temporaryReservationWithId = temporaryReservation.withId(reservationId);

        when(reservationPort.searchByReservationId(reservationId)).thenReturn(temporaryReservationWithId);
        when(queueTokenPort.isActive(token, userId, scheduleId)).thenReturn(true);

        PaymentCommand paymentCommand = new PaymentCommand(token, userId, reservationId);

        assertThrows(IllegalStateException.class, () -> paymentInteractor.pay(paymentCommand));

        // then
        verify(reservationPort).searchByReservationId(reservationId);
        verify(queueTokenPort).isActive(token, userId, temporaryReservationWithId.getScheduleId());
        verifyNoInteractions(seatPort, pointPort, paymentRepositoryPort);

    }

    /**
     *
     */
    @Test
    void payment_fails_when_reservation_status_is_not_temporary() {

        // given
        long userId = 4L;
        long scheduleId = 40L;
        long seatId = 400L;
        long reservationId = 4000L;
        String token = "testToken";

        // when
        Reservation reservation = Reservation.reservedReservation(reservationId, userId, scheduleId, seatId); // 이미 예약 상태의 좌석을 결제 시도

        when(reservationPort.searchByReservationId(reservationId)).thenReturn(reservation);
        when(queueTokenPort.isActive(token, userId, scheduleId)).thenReturn(true);

        PaymentCommand paymentCommand = new PaymentCommand(token, userId, reservationId);

        //
        assertThrows(IllegalStateException.class, () -> paymentInteractor.pay(paymentCommand));

        // then
        verify(reservationPort).searchByReservationId(reservationId);
        verify(queueTokenPort).isActive(token, userId, scheduleId);
        verifyNoInteractions(seatPort, pointPort, paymentRepositoryPort);

    }

    /**
     *
     */
    @Test
    void payment_fails_when_reservation_is_already_expired() {

        // given
        long userId = 5L;
        long scheduleId = 50L;
        long seatId = 500L;
        long reservationId = 5000L;
        String token = "testToken";

        Instant expiresAt = now.minusSeconds(1); // 좌석 예약 (임시 배정) 만료 상황 세팅 (expiresAt < now)

        // when
        Reservation expiredTemporaryReservation = Reservation.temporaryReservation(userId, scheduleId, seatId, expiresAt);
        Reservation expiredTemporaryReservationWithId = expiredTemporaryReservation.withId(reservationId);

        when(reservationPort.searchByReservationId(reservationId)).thenReturn(expiredTemporaryReservation);
        when(queueTokenPort.isActive(token, userId, scheduleId)).thenReturn(true);

        PaymentCommand paymentCommand = new PaymentCommand(token, userId, reservationId);

        //
        assertThrows(IllegalStateException.class, () -> paymentInteractor.pay(paymentCommand));

        // then
        verify(reservationPort).searchByReservationId(reservationId);
        verify(queueTokenPort).isActive(token, userId, scheduleId);
        verifyNoInteractions(seatPort, pointPort, paymentRepositoryPort);

    }

    /**
     *
     */
    @Test
    void payment_fails_when_userPoint_is_insufficient() {

        // given
        long userId = 6L;
        long scheduleId = 60L;
        long seatId = 600L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);
        long reservationId = 6000L;
        String token = "testToken";

        long currentUserPoint = 60000L;
        long seatPrice = 66666L;

        // when
        Reservation temporaryReservation =  Reservation.temporaryReservation(userId, scheduleId, seatId, expiresAt);
        Reservation temporaryReservationWithId = temporaryReservation.withId(reservationId);

        when(reservationPort.searchByReservationId(reservationId)).thenReturn(temporaryReservationWithId);
        when(queueTokenPort.isActive(token, userId, temporaryReservationWithId.getScheduleId())).thenReturn(true);

        when(seatPort.searchSeatPrice(scheduleId, seatId)).thenReturn(seatPrice);

        Point userPoint = new Point(userId, currentUserPoint);

        when(pointPort.searchUserPoint(userId)).thenReturn(userPoint);

        PaymentCommand paymentCommand = new PaymentCommand(token, userId, reservationId);

        assertThrows(IllegalStateException.class, () -> paymentInteractor.pay(paymentCommand));

        // then
        verify(reservationPort).searchByReservationId(reservationId);
        verify(queueTokenPort).isActive(token, userId, scheduleId);
        verify(seatPort).searchSeatPrice(scheduleId, seatId);
        verify(pointPort).searchUserPoint(userId);

        verify(pointPort, never()).deduct(anyLong(), anyLong());
        verify(reservationPort, never()).updateReservationStatus(anyLong(), any(ReservationStatus.class));
        verify(seatPort, never()).updateSeatStatus(anyLong(), anyLong(), any(SeatStatus.class));
        verify(queueTokenPort, never()).expire(anyString());

        verifyNoInteractions(paymentRepositoryPort);

    }

    /**
     *
     */
    @Test
    void payment_success_deduct_userPoints_save_payment_data_update_seat_reservation_status_inactive_queueToken_status() {

        // given
        long userId = 7L;
        long scheduleId = 70L;
        long seatId = 700L;
        Instant expiresAt = seatHoldPolicy.expiresAt(now);
        long reservationId = 7000L;
        String token = "testToken";

        long beforeUserPoint = 77777L;
        long seatPrice = 70000L;
        long afterUserPoint = 7777L;

        long paymentId = 777777L;

        // when
        Reservation temporaryReservation =  Reservation.temporaryReservation(userId, scheduleId, seatId, expiresAt);
        Reservation temporaryReservationWithId = temporaryReservation.withId(reservationId);

        when(reservationPort.searchByReservationId(reservationId)).thenReturn(temporaryReservationWithId);
        when(queueTokenPort.isActive(token, userId, temporaryReservationWithId.getScheduleId())).thenReturn(true);

        when(seatPort.searchSeatPrice(scheduleId, seatId)).thenReturn(seatPrice);

        when(pointPort.searchUserPoint(userId)).thenReturn(new Point(userId, beforeUserPoint));
        when(pointPort.deduct(userId, seatPrice)).thenReturn(new Point(userId, afterUserPoint));

        Payment payment = Payment.payment(reservationId, userId, seatPrice, now);
        Payment paymentWithId = payment.withId(paymentId);

        when(paymentRepositoryPort.pay(any(Payment.class))).thenReturn(paymentWithId);

        PaymentCommand paymentCommand = new PaymentCommand(token, userId, reservationId);

        PaymentResult paymentResult = paymentInteractor.pay(paymentCommand);

        // then

        // 호출 순서
        InOrder inOrder = inOrder(reservationPort, queueTokenPort, seatPort, pointPort, paymentRepositoryPort);
        inOrder.verify(reservationPort).searchByReservationId(reservationId);
        inOrder.verify(queueTokenPort).isActive(token, userId, scheduleId);
        inOrder.verify(seatPort).searchSeatPrice(scheduleId, seatId);
        inOrder.verify(pointPort).searchUserPoint(userId);
        inOrder.verify(pointPort).deduct(userId, seatPrice);
        inOrder.verify(paymentRepositoryPort).pay(any(Payment.class));
        inOrder.verify(reservationPort).updateReservationStatus(reservationId, ReservationStatus.RESERVED);
        inOrder.verify(seatPort).updateSeatStatus(scheduleId, seatId, SeatStatus.RESERVED);
        inOrder.verify(queueTokenPort).expire(token);

        // pay() 에 전달된 Payment 정밀 검증
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepositoryPort).pay(captor.capture());
        Payment requestedPayment = captor.getValue();

        assertEquals(reservationId, requestedPayment.getReservationId());
        assertEquals(userId, requestedPayment.getUserId());
        assertEquals(seatPrice, requestedPayment.getAmount());
        assertEquals(now, requestedPayment.getPaidAt());

    }

}
