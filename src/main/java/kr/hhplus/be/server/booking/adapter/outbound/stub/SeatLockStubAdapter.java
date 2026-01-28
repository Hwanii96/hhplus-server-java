package kr.hhplus.be.server.booking.adapter.outbound.stub;

import kr.hhplus.be.server.booking.port.outbound.SeatLockPort;

import java.time.Instant;

public class SeatLockStubAdapter implements SeatLockPort {

    @Override
    public boolean hold(long userId, long scheduleId, long seatId, Instant expiresAt) {
        return false;
    }

}
