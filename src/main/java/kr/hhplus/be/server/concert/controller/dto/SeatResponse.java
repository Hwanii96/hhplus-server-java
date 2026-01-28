package kr.hhplus.be.server.concert.controller.dto;

import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.concert.entity.SeatEntity;

public record SeatResponse
        (
                long seatId,
                long seatNumber,
                SeatStatus seatStatus,
                long seatPrice
        )
{
    public static SeatResponse from(SeatEntity seatEntity) {

        return new SeatResponse
               (
                       seatEntity.getId(),
                       seatEntity.getSeatNumber(),
                       seatEntity.getSeatStatus(),
                       seatEntity.getSeatPrice()
               );

    }

}
