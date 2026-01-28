package kr.hhplus.be.server.booking.adapter.inbound.controller.dto;

public record ReservationRequest
        (
                long concertId,
                long scheduleId,
                long seatId,
                String queueToken
        )
{

}
