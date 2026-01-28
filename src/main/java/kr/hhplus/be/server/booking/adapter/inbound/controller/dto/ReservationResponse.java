package kr.hhplus.be.server.booking.adapter.inbound.controller.dto;

import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;

import java.time.Instant;

public record ReservationResponse
        (
                long reservationId,
                SeatStatus seatStatus,
                Instant reservationExpiresAt
        )
{

}
