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

    private Reservation(Long id, long userId, long scheduleId, long seatId, ReservationStatus reservationStatus, Instant reservationExpiresAt) {
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

    // 정적 팩토리 메서드 (static factory method)
    // Reservation의 생성자가 public인 경우 객체의 정책에 어긋나게 만들어질 수 있으므로 클린 아키텍처에서는 생성자를 private로 놓고 정적 팩토리로 생성하는 방식으로 작성할 수 있다
    public static Reservation temporaryReservation(long userId, long scheduleId, long seatId, Instant reservationExpiresAt) {
        return new Reservation(null, userId, scheduleId, seatId, ReservationStatus.TEMPORARY, reservationExpiresAt);
    }

    // Port 호출 결과로 id 값을 포함하여 새로운 Reservation이 반환될 때 id는 불변 필드 이므로 이 부분을 해결하기 위한 메서드로, 새로운 Reservation 객체를 생성하도록 하는 패턴이다
    public Reservation withId(Long id) {
        return new Reservation(id, this.userId, this.scheduleId, this.seatId, this.reservationStatus, this.reservationExpiresAt);
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
