package kr.hhplus.be.server.booking.adapter.inbound.controller.dto;

public record PaymentRequest
        (
                long reservationId,
                String queueToken
        )
{

}
