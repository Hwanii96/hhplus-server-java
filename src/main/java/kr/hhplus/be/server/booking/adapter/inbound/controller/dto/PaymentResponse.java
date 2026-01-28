package kr.hhplus.be.server.booking.adapter.inbound.controller.dto;

import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;

import java.time.Instant;

public record PaymentResponse
        (
                long paymentId,
                SeatStatus seatStatus,
                long paidAmount,
                long userCurrentPoints,
                Instant paidAt
        )
{
}
