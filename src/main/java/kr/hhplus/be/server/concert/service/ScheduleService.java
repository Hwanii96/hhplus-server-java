package kr.hhplus.be.server.concert.service;

import kr.hhplus.be.server.concert.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.concert.repository.ConcertScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ScheduleService {

    private final ConcertScheduleRepository concertScheduleRepository;

    public ScheduleService(ConcertScheduleRepository concertScheduleRepository) {
        this.concertScheduleRepository = concertScheduleRepository;
    }

    @Transactional(readOnly = true)
    public ConcertScheduleEntity getSchedule(long scheduleId) {

        if (scheduleId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid scheduleId");
        }

        return concertScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found"));
    }

    @Transactional(readOnly = true)
    public List<ConcertScheduleEntity> getSchedulesByConcertId(long concertId) {
        return concertScheduleRepository.findByConcertIdOrderByStartAtAsc(concertId);
    }

}
