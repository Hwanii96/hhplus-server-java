package kr.hhplus.be.server.concert.entity;

import jakarta.persistence.*;

import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;

@Entity
@Table
        (
                name = "concerts_schedules_seats",
                uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "seat_number"})
        )
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private long scheduleId;

    @Column(name = "seat_number", nullable = false)
    private long seatNumber;

    @Column(name = "seat_price", nullable = false)
    private long seatPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", nullable = false)
    private SeatStatus seatStatus;

    protected SeatEntity() { // JPA 기본 생성자

    }

    private SeatEntity(long scheduleId, int seatNumber, long seatPrice, SeatStatus seatStatus) {
        this.scheduleId = scheduleId;
        this.seatNumber = seatNumber;
        this.seatPrice = seatPrice;
        this.seatStatus = seatStatus;
    }

    public static SeatEntity createSeat(long scheduleId, int seatNumber, long seatPrice) {
        return new SeatEntity(scheduleId, seatNumber, seatPrice, SeatStatus.AVAILABLE);
    }

    public void changeStatus(SeatStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

    public Long getId() {
        return id;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public long getSeatNumber() {
        return seatNumber;
    }

    public long getSeatPrice() {
        return seatPrice;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }

}
