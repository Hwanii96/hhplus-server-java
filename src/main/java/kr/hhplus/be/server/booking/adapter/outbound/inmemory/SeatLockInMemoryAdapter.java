package kr.hhplus.be.server.booking.adapter.outbound.inmemory;

import kr.hhplus.be.server.booking.infrastructure.inmemory.RedisInMemoryStore;
import kr.hhplus.be.server.booking.port.outbound.SeatLockPort;

import java.time.Instant;
import java.util.function.Supplier;

public class SeatLockInMemoryAdapter implements SeatLockPort {

    private final RedisInMemoryStore redisInMemoryStore;
    private final Supplier<Instant> nowProvider;

    public SeatLockInMemoryAdapter(RedisInMemoryStore redisInMemoryStore, Supplier<Instant> nowProvider) {
        this.redisInMemoryStore = redisInMemoryStore;
        this.nowProvider = nowProvider;
    }

    @Override
    public boolean hold(long userId, long scheduleId, long seatId, Instant expiresAt) {
        return redisInMemoryStore.tryHold(userId, scheduleId, seatId, expiresAt, nowProvider.get());
    }

}
