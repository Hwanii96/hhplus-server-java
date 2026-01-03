package kr.hhplus.be.server.booking.application.service;

import kr.hhplus.be.server.booking.application.command.PaymentCommand;
import kr.hhplus.be.server.booking.application.result.PaymentResult;
import kr.hhplus.be.server.booking.domain.model.entity.Reservation;
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
            throw new IllegalStateException("the reservation is not available");
        }

        // throw new UnsupportedOperationException("not implement code yet");

        long scheduleId = reservation.getScheduleId();

        boolean isActive = queueTokenPort.isActive(paymentCommand.queueToken(), paymentCommand.userId(), scheduleId);

        if (!isActive) {
            throw new IllegalStateException("queueToken is not ACTIVE status");
        }

        throw new UnsupportedOperationException("not implement code yet");

    } // pay()

} // PaymentInteractor
