package kr.hhplus.be.server.booking.port.outbound;

public interface QueueTokenPort {

    boolean isActive(String token, long userId, long scheduleId);

}
