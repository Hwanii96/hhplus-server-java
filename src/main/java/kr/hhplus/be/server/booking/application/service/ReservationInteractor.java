package kr.hhplus.be.server.booking.application.service;

import kr.hhplus.be.server.booking.application.command.ReservationCommand;
import kr.hhplus.be.server.booking.domain.policy.SeatHoldPolicy;
import kr.hhplus.be.server.booking.port.outbound.QueueTokenPort;
import kr.hhplus.be.server.booking.port.outbound.ReservationPort;
import kr.hhplus.be.server.booking.port.outbound.SeatLockPort;

import java.time.Instant;
import java.util.function.Supplier;

public class ReservationInteractor {

    private final QueueTokenPort queueTokenPort;
    private final SeatLockPort seatLockPort;
    private final ReservationPort reservationPort;
    private final SeatHoldPolicy seatHoldPolicy;
    private final Supplier<Instant> nowProvider;

    public ReservationInteractor
            (
                    QueueTokenPort queueTokenPort,
                    SeatLockPort seatLockPort,
                    ReservationPort reservationPort,
                    SeatHoldPolicy seatHoldPolicy,
                    Supplier<Instant> nowProvider
            )
    {
        this.queueTokenPort = queueTokenPort;
        this.seatLockPort = seatLockPort;
        this.reservationPort = reservationPort;
        this.seatHoldPolicy = seatHoldPolicy;
        this.nowProvider = nowProvider;
    }

    public void reserve(ReservationCommand reservationCommand) {

        boolean isActive = queueTokenPort.isActive
                (
                        reservationCommand.queueToken(),
                        reservationCommand.userId(),
                        reservationCommand.scheduleId()
                );

        if(!isActive) {
            throw new IllegalStateException("queueToken is not ACTIVE status");
        }
        else {
            throw new UnsupportedOperationException("not implement code yet");
        }

    } // reserve()

} // ReservationInteractor
