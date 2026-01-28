package kr.hhplus.be.server.booking.infrastructure.inmemory;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisInMemoryStore {

    public record TokenKey(String token) {}
    public record TokenValue(long userId, long scheduleId, boolean active) {}

    public record SeatKey(long scheduleId, long seatId) {}
    public record HoldValue(long userId, Instant expiresAt) {}

    private final Map<TokenKey, TokenValue> tokens = new ConcurrentHashMap<>();
    private final Map<SeatKey, HoldValue> holds = new ConcurrentHashMap<>();

    // ---- token ----
    public void putActiveToken(String token, long userId, long scheduleId) {

        tokens.put(new TokenKey(token), new TokenValue(userId, scheduleId, true));

    }

    public boolean isTokenActive(String token, long userId, long scheduleId) {

        TokenValue tokenValue = tokens.get(new TokenKey(token));

        return tokenValue != null && tokenValue.active() && tokenValue.userId() == userId && tokenValue.scheduleId() == scheduleId;

    }

    public void expireToken(String token) {

        TokenKey key = new TokenKey(token);

        TokenValue tokenValue = tokens.get(key);

        if (tokenValue != null) {
            tokens.put(key, new TokenValue(tokenValue.userId(), tokenValue.scheduleId(), false));
        }

    }

    // ---- hold(lock) ----
    public boolean tryHold(long userId, long scheduleId, long seatId, Instant expiresAt, Instant now) {

        SeatKey key = new SeatKey(scheduleId, seatId);

        HoldValue finalValue = holds.compute(key, (k, existing) -> {

            if (existing == null) return new HoldValue(userId, expiresAt);

            // 만료되었으면 교체 가능
            if (!now.isBefore(existing.expiresAt())) {
                return new HoldValue(userId, expiresAt);
            }

            // 아직 유효한 hold면 유지
            return existing;

        });

        // 최종 소유자가 나면 true
        return finalValue.userId() == userId;

    }

    // (옵션) 결제/취소 흐름에서 강제 해제 필요할 경우를 위함
    public void releaseHold(long scheduleId, long seatId, long userId) {

        SeatKey key = new SeatKey(scheduleId, seatId);

        holds.computeIfPresent(key, (k, existing) -> {

            if (existing.userId() == userId) return null; // remove
            return existing;

        });

    }

}
