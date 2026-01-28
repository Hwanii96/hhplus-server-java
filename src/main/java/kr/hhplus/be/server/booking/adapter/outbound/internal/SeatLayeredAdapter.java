package kr.hhplus.be.server.booking.adapter.outbound.internal;

import org.springframework.transaction.annotation.Transactional;
import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.booking.port.outbound.SeatPort;
import kr.hhplus.be.server.concert.service.SeatService;

/**
 * SeatPort를 구현하는 Adapter 구현체로 레거시 레이어드 아키텍처로 존재하는 Seat domain을 내부적으로 호출함
 * 따라서, booking.adapter.outbound.internal 위치에 놓도록 함
 */
public class SeatLayeredAdapter implements SeatPort {

    private final SeatService seatService;

    public SeatLayeredAdapter(SeatService seatService) {
        this.seatService = seatService;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(long scheduleId, long seatId) {
        return seatService.isAvailable(scheduleId, seatId);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatStatus searchSeatStatus(long scheduleId, long seatId) {
        return seatService.searchSeatStatus(scheduleId, seatId);
    }

    @Override
    @Transactional(readOnly = true)
    public long searchSeatPrice(long scheduleId, long seatId) {
        return seatService.searchSeatPrice(scheduleId, seatId);
    }

    @Override
    @Transactional
    public void updateSeatStatus(long scheduleId, long seatId, SeatStatus seatStatus) {
        seatService.updateSeatStatus(scheduleId, seatId, seatStatus);
    }

}
