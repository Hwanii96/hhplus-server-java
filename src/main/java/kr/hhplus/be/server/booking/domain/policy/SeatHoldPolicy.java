package kr.hhplus.be.server.booking.domain.policy;

import java.time.Duration;
import java.time.Instant;

public class SeatHoldPolicy {

    private final Duration holdDuration;

    public SeatHoldPolicy(Duration holdDuration) {
        this.holdDuration = holdDuration;
    }

    public Instant expiresAt(Instant now) {
        return now.plus(holdDuration);
    }

}
