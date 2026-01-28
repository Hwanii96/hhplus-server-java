package kr.hhplus.be.server.booking.application.exception.payment;

public class ReservationOwnerMismatchException extends  RuntimeException {

    public ReservationOwnerMismatchException(String message) {
        super(message);
    }

}
