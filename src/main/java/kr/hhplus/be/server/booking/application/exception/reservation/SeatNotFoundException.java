package kr.hhplus.be.server.booking.application.exception.reservation;

public class SeatNotFoundException extends IllegalStateException {

    public SeatNotFoundException() {
        super("seat not found");
    }

}
