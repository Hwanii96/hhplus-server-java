package kr.hhplus.be.server.concert.service;

import kr.hhplus.be.server.concert.controller.dto.ScheduleResponse;
import org.springframework.transaction.annotation.Transactional;
import kr.hhplus.be.server.concert.controller.dto.SeatResponse;
import kr.hhplus.be.server.concert.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.concert.entity.SeatEntity;
import kr.hhplus.be.server.concert.repository.ConcertScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConcertService {

    private final ScheduleService scheduleService;
    private final SeatService seatService;
    // private final ConcertScheduleRepository concertScheduleRepository; // 수정 필요 -> repository 직접 사용 x -> service 사용 o

    public ConcertService(ScheduleService scheduleService, SeatService seatService, ConcertScheduleRepository concertScheduleRepository) {
        this.scheduleService = scheduleService;
        this.seatService = seatService;
        // this.concertScheduleRepository = concertScheduleRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedules(long concertId) {

        if (concertId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid concertId");
        }

        List<ConcertScheduleEntity> concertSchedules = scheduleService.getSchedulesByConcertId(concertId);

        if (concertSchedules.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "concert schedules not found");
        }

        List<ScheduleResponse> result = new ArrayList<>();
        Instant now = Instant.now();

        for (ConcertScheduleEntity schedules : concertSchedules) {

            long scheduleId = schedules.getId();
            long remaining = seatService.countRemainingSeats(scheduleId); // AVAILABLE count
            boolean reservable = schedules.getStartAt().isAfter(now) && remaining > 0;

            result.add(new ScheduleResponse
                    (
                            scheduleId,
                            schedules.getStartAt(),
                            reservable,
                            remaining
                    )
            );
        }

        return result;
    }

    // 해당 메서드의 행위는 Seat Domain 이지만, 메서드 인자는 concertId와 scheduleId 이므로 (검증) ConcertService에 놓고, SeatService를 호출하도록 함
    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatList(long concertId, long scheduleId) {

        /*
        ConcertScheduleEntity concertSchedule =
                concertScheduleRepository.findById(scheduleId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "schedule not found"));

        */

        ConcertScheduleEntity concertSchedule = scheduleService.getSchedule(scheduleId);

        if(concertSchedule.getConcertId() != concertId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "the schedule does not belong to this concert");
        }

        // return seatRepository.findByScheduleIdOrderBySeatNumberAsc(scheduleId).stream().map(SeatResponse::from).toList();

        // return seatRepository.findByScheduleIdOrderBySeatNumberAsc(scheduleId).stream().map(seatEntity -> SeatResponse.from(seatEntity)).toList();

        /*
        List<SeatEntity> seats = seatRepository.findByScheduleIdOrderBySeatNumberAsc(scheduleId);

        List<SeatResponse> result = new ArrayList<>();

        for (SeatEntity seat : seats) {
            result.add(SeatResponse.from(seat));
        }

        return result;
        */

        // SeatService를 사용하도록 함
        List<SeatEntity> seats = seatService.getSeatListByScheduleId(scheduleId);

        List<SeatResponse> result = new ArrayList<>();

        for(int i = 0; i < seats.size(); i++) {
            SeatEntity seat = seats.get(i);
            result.add(SeatResponse.from(seat));
        }

        return result;
    }

}
