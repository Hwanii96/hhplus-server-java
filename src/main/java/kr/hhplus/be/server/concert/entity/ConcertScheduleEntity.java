package kr.hhplus.be.server.concert.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "concert_schedules")
public class ConcertScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="concert_id", nullable=false)
    private long concertId;

    @Column(name="start_at", nullable=false)
    private Instant startAt;

    protected ConcertScheduleEntity() {

    }

    public Long getId() {
        return id;
    }

    public long getConcertId() {
        return concertId;
    }

    public Instant getStartAt() {
        return startAt;
    }

}
