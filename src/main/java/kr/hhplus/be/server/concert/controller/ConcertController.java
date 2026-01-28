package kr.hhplus.be.server.concert.controller;

import kr.hhplus.be.server.concert.controller.dto.ScheduleResponse;
import kr.hhplus.be.server.concert.controller.dto.SeatResponse;
import kr.hhplus.be.server.concert.service.ConcertService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/concerts")
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    // 예약 가능한 날짜 (스케줄) 조회
    @GetMapping("/{concertId}/schedules")
    public List<ScheduleResponse> getSchedules(@PathVariable long concertId) {
        return concertService.getSchedules(concertId);
    }

    // 좌석 목록 조회
    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    public List<SeatResponse> getSeats(@PathVariable long concertId, @PathVariable long scheduleId) {
        return concertService.getSeatList(concertId, scheduleId);
    }

}
