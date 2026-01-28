package kr.hhplus.be.server.concert.controller.dto;

import java.time.Instant;

public record ScheduleResponse
        (
                long scheduleId,
                Instant startAt,
                boolean isReservable,
                long remainingSeats
        )
{
}
