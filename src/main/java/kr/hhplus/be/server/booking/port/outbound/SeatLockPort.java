package kr.hhplus.be.server.booking.port.outbound;

import java.time.Instant;

/**
 * 좌석 임시 배정 (예약) 만료 시각 설정을 위한 Port
 * DB에 직접 UPDATE 하는 방식 or 그 외의 방법 고려
 * 성공적으로 처리 시 true로 반환되도록 하며, 실패 시 false로 반환되도록 함
 */
public interface SeatLockPort {

    boolean hold(long userId, long scheduleId, long seatId, Instant expiresAt);

}
