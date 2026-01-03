package kr.hhplus.be.server.booking.port.outbound;

import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;

public interface SeatPort {

    boolean isAvailable(long scheduleId, long seatId);

    long searchSeatPrice(long scheduleId, long seatId);

    void updateSeatStatus(long scheduleId, long seatId, SeatStatus seatStatus); // 예 : 결제 (Payment) 성공 시 해당 좌석 상태를 "RESERVED" 로 변경

}
