package kr.hhplus.be.server.booking.application.exception.reservation;

public class SeatNotAvailableException extends IllegalStateException {

    public SeatNotAvailableException() {
        super("this seat is not available");
    }

}
