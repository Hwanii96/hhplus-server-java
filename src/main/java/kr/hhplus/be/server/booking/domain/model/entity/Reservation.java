package kr.hhplus.be.server.booking.domain.model.entity;

import kr.hhplus.be.server.booking.domain.model.enums.ReservationStatus;

import java.time.Instant;
import java.util.Objects;

public class Reservation {

    private final Long id; // Long : null 표현을 위함
    private final long userId;
    private final long scheduleId;
    private final long seatId;

    private final ReservationStatus reservationStatus;
    private final Instant reservationExpiresAt; // reservationStatus 값이 TEMPORARY 인 경우에만 해당

    public Reservation(Long id, long userId, long scheduleId, long seatId, ReservationStatus reservationStatus, Instant reservationExpiresAt) {
        this.id = id;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.seatId = seatId;
        this.reservationStatus = Objects.requireNonNull(reservationStatus, "reservationStatus must not be null"); // reservationStatus : NOT NULL

        if(reservationStatus == ReservationStatus.TEMPORARY) {
            this.reservationExpiresAt = Objects.requireNonNull(reservationExpiresAt, "reservationExpiresAt is required when reservationStatus is TEMPORARY");
        }
        else {
            this.reservationExpiresAt = reservationExpiresAt;
        }
    }

    // 테스트를 위한 정적 팩토리 메서드 (static factory method)
    public static Reservation temporary(long userId, long scheduleId, long seatId, Instant reservationExpiresAt) {
        return new Reservation(null, userId, scheduleId, seatId, ReservationStatus.TEMPORARY, reservationExpiresAt);
    }

    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public long getSeatId() {
        return seatId;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public Instant getReservationExpiresAt() {
        return reservationExpiresAt;
    }

}
