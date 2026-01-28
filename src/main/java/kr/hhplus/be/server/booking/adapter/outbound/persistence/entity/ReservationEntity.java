package kr.hhplus.be.server.booking.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.booking.domain.model.enums.ReservationStatus;

import java.time.Instant;

@Entity
@Table(name = "seats_reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;
    private long scheduleId;
    private long seatId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    private Instant reservationExpiresAt;

    protected ReservationEntity() {

    }

    public ReservationEntity(long userId, long scheduleId, long seatId, ReservationStatus reservationStatus, Instant reservationExpiresAt) {
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.seatId = seatId;
        this.reservationStatus = reservationStatus;
        this.reservationExpiresAt = reservationExpiresAt;
    }

    public void changeStatus(ReservationStatus changeStatus) {
        this.reservationStatus = changeStatus;
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
