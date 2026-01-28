package kr.hhplus.be.server.booking.application.service;

import kr.hhplus.be.server.booking.application.command.PaymentCommand;
import kr.hhplus.be.server.booking.application.exception.payment.*;
import kr.hhplus.be.server.booking.application.exception.reservation.QueueTokenInactiveException;
import kr.hhplus.be.server.booking.application.result.PaymentResult;
import kr.hhplus.be.server.booking.domain.model.entity.Payment;
import kr.hhplus.be.server.booking.domain.model.entity.Point;
import kr.hhplus.be.server.booking.domain.model.entity.Reservation;
import kr.hhplus.be.server.booking.domain.model.enums.ReservationStatus;
import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.booking.port.inbound.PaymentUseCase;
import kr.hhplus.be.server.booking.port.outbound.*;

import java.time.Instant;
import java.util.function.Supplier;

public class PaymentInteractor implements PaymentUseCase {

    private final QueueTokenPort queueTokenPort;
    private final SeatPort seatPort;
    private final ReservationPort reservationPort;
    private final PointPort pointPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final Supplier<Instant> nowProvider; // 좌석 결제 시점에 검증 사항 (1. 예약 상태가 "TEMPORARY" 인지 ? | 2. 예약 만료 시간이 현재 시각 (now) 보다 이전인지 ?)

    public PaymentInteractor
            (
                    QueueTokenPort queueTokenPort,
                    SeatPort seatPort,
                    ReservationPort reservationPort,
                    PointPort pointPort,
                    PaymentRepositoryPort paymentRepositoryPort,
                    Supplier<Instant>nowProvider
            )
    {
        this.queueTokenPort = queueTokenPort;
        this.seatPort = seatPort;
        this.reservationPort = reservationPort;
        this.pointPort = pointPort;
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.nowProvider = nowProvider;
    }

    @Override
    public PaymentResult pay(PaymentCommand paymentCommand) {

        Reservation reservation = reservationPort.searchByReservationId(paymentCommand.reservationId());

        if(reservation == null) {
            throw new ReservationNotFoundException("reservation not found");
            // throw new IllegalStateException("the reservation is not available");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        long scheduleId = reservation.getScheduleId();
        long seatId = reservation.getSeatId();
        long reservationId = reservation.getId();

        boolean isActive = queueTokenPort.isActive(paymentCommand.queueToken(), paymentCommand.userId(), scheduleId);

        if (!isActive) {
            throw new QueueTokenInactiveException("queueToken is not ACTIVE status"); // exception.reservation에 있는 예외 클래스를 사용함
            // throw new IllegalStateException("queueToken is not ACTIVE status");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        if(reservation.getUserId() != paymentCommand.userId()) {
            throw new ReservationOwnerMismatchException("reservation owner is not match");
            // throw new IllegalStateException("reservation owner is not match");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        if(reservation.getReservationStatus() != ReservationStatus.TEMPORARY) {
            throw new ReservationStatusNotTemporaryException("reservation status is not TEMPORARY");
            // throw new IllegalStateException("reservation status is not TEMPORARY");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        Instant now = nowProvider.get();
        Instant expiresAt = reservation.getReservationExpiresAt();

        if(expiresAt.isBefore(now)) {
            throw new ReservationExpiredException("reservation is already expired");
            // throw new IllegalStateException("reservation is already expired");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        SeatStatus currentSeatStatus = seatPort.searchSeatStatus(scheduleId, seatId);

        if (currentSeatStatus == SeatStatus.RESERVED) {
            throw new ReservationStatusNotTemporaryException("seat is already RESERVED");
        }


        long seatPrice = seatPort.searchSeatPrice(scheduleId, reservation.getSeatId());

        Point userPoint = pointPort.searchUserPoint(paymentCommand.userId());

        if(userPoint == null) {
            throw new PointNotFoundException("user point not found");
            // throw new IllegalStateException("user point not found");
        }

        long userCurrentPoint = userPoint.getPoint();

        if(userCurrentPoint < seatPrice) {
            throw new InsufficientPointException("userPoint is not sufficient");
            // throw new IllegalStateException("userPoint is not sufficient");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        Point afterUserPoint = pointPort.deduct(paymentCommand.userId(), seatPrice);

        Payment payment = Payment.payment(reservationId, paymentCommand.userId(), seatPrice, now);

        Payment result = paymentRepositoryPort.pay(payment);

        if(result.getId() == null) {
            throw new IllegalStateException("payment id must not be null");
        }

        reservationPort.updateReservationStatus(reservationId, ReservationStatus.RESERVED);
        seatPort.updateSeatStatus(scheduleId, seatId, SeatStatus.RESERVED);
        queueTokenPort.expire(paymentCommand.queueToken());

        return new PaymentResult
                (
                        result.getId(),
                        SeatStatus.RESERVED,
                        seatPrice,
                        afterUserPoint.getPoint(),
                        now
                );

    } // pay()

} // PaymentInteractor
