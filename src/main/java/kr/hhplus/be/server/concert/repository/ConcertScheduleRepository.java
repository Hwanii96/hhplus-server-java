package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.concert.entity.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertScheduleRepository extends JpaRepository<ConcertScheduleEntity, Long> {

    List<ConcertScheduleEntity> findByConcertIdOrderByStartAtAsc(long concertId);

}
