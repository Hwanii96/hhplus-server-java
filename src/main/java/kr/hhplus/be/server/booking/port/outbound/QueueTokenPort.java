package kr.hhplus.be.server.booking.port.outbound;

public interface QueueTokenPort {

    boolean isActive(String queueToken, long userId, long scheduleId);

    void expire(String queueToken); // 결제 (Payment) 성공 시 대기열 토큰 만료 처리

}
