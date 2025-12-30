package kr.hhplus.be.server.booking.port.outbound;

public interface SeatAvailabilityPort {

    boolean isAvailable(long scheduleId, long seatId);

}
