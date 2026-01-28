package kr.hhplus.be.server.concert.service;

import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.concert.entity.SeatEntity;
import kr.hhplus.be.server.concert.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(long scheduleId, long seatId) {

        SeatEntity seat = seatRepository.findByIdAndScheduleId(seatId, scheduleId)
                .orElseThrow(() -> new IllegalStateException("seat not found"));

        return SeatStatus.AVAILABLE.equals(seat.getSeatStatus());
    }

    @Transactional(readOnly = true)
    public SeatStatus searchSeatStatus(long scheduleId, long seatId) {

        SeatEntity seat = seatRepository.findByIdAndScheduleId(seatId, scheduleId)
                .orElseThrow(() -> new IllegalStateException("seat not found"));

        return seat.getSeatStatus();
    }

    @Transactional(readOnly = true)
    public long searchSeatPrice(long scheduleId, long seatId) {

        SeatEntity seat = seatRepository.findByIdAndScheduleId(seatId, scheduleId)
                .orElseThrow(() -> new IllegalStateException("seat not found"));

        return seat.getSeatPrice();
    }

    @Transactional
    public void updateSeatStatus(long scheduleId, long seatId, SeatStatus seatStatus) {

        SeatEntity seat = seatRepository.findByIdAndScheduleId(seatId, scheduleId)
                .orElseThrow(() -> new IllegalStateException("seat not found"));

        // @Transactional 범위 내이므로 더티 체킹으로 UPDATE 반영
        seat.changeStatus(seatStatus);

    }

    @Transactional(readOnly = true)
    public List<SeatEntity> getSeatListByScheduleId(long scheduleId) {

        return seatRepository.findByScheduleIdOrderBySeatNumberAsc(scheduleId);

    }

    @Transactional(readOnly = true)
    public long countRemainingSeats(long scheduleId) {
        return seatRepository.countByScheduleIdAndSeatStatus(scheduleId, SeatStatus.AVAILABLE);
    }

}
