package kr.hhplus.be.server.booking.adapter.outbound.persistence.adapter;

import kr.hhplus.be.server.booking.adapter.outbound.persistence.entity.PaymentEntity;
import kr.hhplus.be.server.booking.adapter.outbound.persistence.repository.PaymentJpaRepository;
import kr.hhplus.be.server.booking.domain.model.entity.Payment;
import kr.hhplus.be.server.booking.port.outbound.PaymentRepositoryPort;
import org.springframework.transaction.annotation.Transactional;

public class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentPersistenceAdapter(PaymentJpaRepository paymentJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    @Transactional
    public Payment pay(Payment payment) {

        PaymentEntity paymentEntity = new PaymentEntity
                (
                        payment.getReservationId(),
                        payment.getUserId(),
                        payment.getAmount(),
                        payment.getPaidAt()
                );

        PaymentEntity paymentEntitySaved = paymentJpaRepository.save(paymentEntity);

        return payment.withId(paymentEntitySaved.getId());

    }

}
