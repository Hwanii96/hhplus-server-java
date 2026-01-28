package kr.hhplus.be.server.booking.adapter.outbound.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long reservationId;
    private long userId;
    private long paidAmount;

    private Instant paidAt;

    protected PaymentEntity() {

    }

    public PaymentEntity(long reservationId, long userId, long paidAmount, Instant paidAt) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.paidAmount = paidAmount;
        this.paidAt = paidAt;
    }

    public Long getId() {
        return id;
    }

    public long getReservationId() {
        return reservationId;
    }

    public long getUserId() {
        return userId;
    }

    public long getPaidAmount() {
        return paidAmount;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

}
