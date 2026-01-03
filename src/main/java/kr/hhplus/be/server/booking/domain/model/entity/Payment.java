package kr.hhplus.be.server.booking.domain.model.entity;

import kr.hhplus.be.server.booking.domain.model.enums.PaymentStatus;
import kr.hhplus.be.server.booking.domain.model.enums.ReservationStatus;

import java.time.Instant;
import java.util.Objects;

public class Payment {

    private final Long id; // Long : null 표현을 위함
    private final long reservationId;
    private final long userId;
    private final long amount;

    private final PaymentStatus paymentStatus;
    private final Instant paidAt;

    private Payment(Long id, long reservationId, long userId, long amount, PaymentStatus paymentStatus, Instant paidAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.userId = userId;
        this.amount = amount;
        this.paymentStatus = Objects.requireNonNull(paymentStatus, "paymentStatus must not be null"); // paymentStatus : NOT NULL

        if(paymentStatus == PaymentStatus.SUCCESS) {
            this.paidAt = Objects.requireNonNull(paidAt, "paidAt is required when paymentStatus is SUCCESS");
        }
        else {
            this.paidAt = Objects.requireNonNull(paidAt);
        }
    }

    // 정적 팩토리 메서드 (static factory method)
    // Reservation의 생성자가 public인 경우 객체의 정책에 어긋나게 만들어질 수 있으므로 클린 아키텍처에서는 생성자를 private로 놓고 정적 팩토리로 생성하는 방식으로 작성할 수 있다
    public static Payment payment(long reservationId, long userId, long amount, Instant paidAt) {
        return new Payment(null, reservationId, userId, amount, PaymentStatus.SUCCESS, paidAt);
    }

    // Port 호출 결과로 id 값을 포함하여 새로운 Payment 반환될 때 id는 불변 필드 이므로 이 부분을 해결하기 위한 메서드로, 새로운 Payment 객체를 생성하도록 하는 패턴이다
    public Payment withId(Long id) {
        return new Payment(id, this.reservationId, this.userId, this.amount, this.paymentStatus, this.paidAt);
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

    public long getAmount() {
        return amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

}
