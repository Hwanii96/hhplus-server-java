package kr.hhplus.be.server.point.repository;

import kr.hhplus.be.server.point.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<PointEntity, Long> {

    // PK가 userId 라서 findById (userId) 로 충분하지만 가독성을 위해 작성함
    Optional<PointEntity> findByUserId(long userId);

}
