package kr.hhplus.be.server.booking.application.exception.reservation;

public class SeatAlreadyHeldException extends IllegalStateException {

    public SeatAlreadyHeldException() {
        super("this seat is already held");
    }

}
