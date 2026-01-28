package kr.hhplus.be.server.booking.adapter.outbound.persistence.repository;

import kr.hhplus.be.server.booking.adapter.outbound.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository (기술 컴포넌트)
 * PaymentEntity (JPA Entity) 기반 CRUD 제공
 * 매핑은 PersistenceAdapter 또는 별도의 Mapper에서 처리
 */
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

}
