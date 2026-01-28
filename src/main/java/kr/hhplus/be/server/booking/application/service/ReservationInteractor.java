package kr.hhplus.be.server.booking.application.service;

import kr.hhplus.be.server.booking.application.command.ReservationCommand;
import kr.hhplus.be.server.booking.application.result.ReservationResult;
import kr.hhplus.be.server.booking.domain.model.entity.Reservation;
import kr.hhplus.be.server.booking.domain.policy.SeatHoldPolicy;
import kr.hhplus.be.server.booking.port.inbound.ReservationUseCase;
import kr.hhplus.be.server.booking.port.outbound.QueueTokenPort;
import kr.hhplus.be.server.booking.port.outbound.ReservationPort;
import kr.hhplus.be.server.booking.port.outbound.SeatPort;
import kr.hhplus.be.server.booking.port.outbound.SeatLockPort;

import java.time.Instant;
import java.util.function.Supplier;

public class ReservationInteractor implements ReservationUseCase {

    private final QueueTokenPort queueTokenPort;
    private final SeatPort seatPort;
    private final SeatLockPort seatLockPort;
    private final ReservationPort reservationPort;
    private final SeatHoldPolicy seatHoldPolicy;
    private final Supplier<Instant> nowProvider;

    public ReservationInteractor
            (
                    QueueTokenPort queueTokenPort,
                    SeatPort seatPort,
                    SeatLockPort seatLockPort,
                    ReservationPort reservationPort,
                    SeatHoldPolicy seatHoldPolicy,
                    Supplier<Instant> nowProvider
            )
    {
        this.queueTokenPort = queueTokenPort;
        this.seatPort = seatPort;
        this.seatLockPort = seatLockPort;
        this.reservationPort = reservationPort;
        this.seatHoldPolicy = seatHoldPolicy;
        this.nowProvider = nowProvider;
    }

    public ReservationResult reserve(ReservationCommand reservationCommand) {

        boolean isActive = queueTokenPort.isActive
                (
                        reservationCommand.queueToken(),
                        reservationCommand.userId(),
                        reservationCommand.scheduleId()
                );

        if(!isActive) {
            throw new IllegalStateException("queueToken is not ACTIVE status");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        boolean available = seatPort.isAvailable
                (
                        reservationCommand.scheduleId(),
                        reservationCommand.seatId()
                );

        if(!available) {
            throw new IllegalStateException("this seat is no available");
        }

        Instant expiresAt = seatHoldPolicy.expiresAt(nowProvider.get());

        boolean held = seatLockPort.hold
                (
                        reservationCommand.userId(),
                        reservationCommand.scheduleId(),
                        reservationCommand.seatId(),
                        expiresAt
                );

        if(!held) {
            throw new IllegalStateException("seat is already held");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        Reservation temporaryReservation = Reservation.temporaryReservation
                (
                        reservationCommand.userId(),
                        reservationCommand.scheduleId(),
                        reservationCommand.seatId(),
                        expiresAt
                );

        Reservation reservation = reservationPort.reserve(temporaryReservation);

        Long id = reservation.getId();

        if(id == null) {
            throw new IllegalStateException("reservation id is must not be null");
        }

        // expiresAt : DB에서 가져온 expiresAt를 사용하면 정합성이 깨질 수 있으며, 만료 시각 정책은 Interactor가 주체이므로, reservation.getReservationExpiresAt() 로 사용하지 않는다
        return new ReservationResult(id, expiresAt);

        // throw new UnsupportedOperationException("not implement code yet");

    } // reserve()

} // ReservationInteractor
