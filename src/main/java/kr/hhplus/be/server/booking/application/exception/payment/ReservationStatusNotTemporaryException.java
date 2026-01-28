package kr.hhplus.be.server.booking.application.exception.payment;

public class ReservationStatusNotTemporaryException extends RuntimeException {

    public ReservationStatusNotTemporaryException(String message) {
        super(message);
    }

}
