package kr.hhplus.be.server.booking.adapter.outbound.inmemory;

import kr.hhplus.be.server.booking.infrastructure.inmemory.RedisInMemoryStore;
import kr.hhplus.be.server.booking.port.outbound.QueueTokenPort;

public class QueueTokenInMemoryAdapter implements QueueTokenPort {

    private final RedisInMemoryStore redisInMemoryStore;

    public QueueTokenInMemoryAdapter(RedisInMemoryStore redisInMemoryStore) {
        this.redisInMemoryStore = redisInMemoryStore;
    }

    @Override
    public boolean isActive(String queueToken, long userId, long scheduleId) {
        return redisInMemoryStore.isTokenActive(queueToken, userId, scheduleId);
    }

    @Override
    public void expire(String queueToken) {
        redisInMemoryStore.expireToken(queueToken);
    }

}
