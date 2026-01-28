package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.concert.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {

    // scheduleId로 50석 목록 조회
    List<SeatEntity> findByScheduleIdOrderBySeatNumberAsc(long scheduleId);

    // 특정 좌석 1건을 조회
    Optional<SeatEntity> findByIdAndScheduleId(Long id, Long scheduleId);

    //
    long countByScheduleIdAndSeatStatus(long scheduleId, SeatStatus seatStatus);

    // 스케줄 (일정) 이 존재하는지 검증
    boolean existsByScheduleId(long scheduleId);

}
