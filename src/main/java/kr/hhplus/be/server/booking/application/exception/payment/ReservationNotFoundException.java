package kr.hhplus.be.server.booking.application.exception.payment;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(String message) {
        super(message);
    }

}
