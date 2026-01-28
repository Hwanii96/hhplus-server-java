package kr.hhplus.be.server.booking.application.exception.payment;

public class ReservationExpiredException extends RuntimeException{

    public ReservationExpiredException(String message) {
        super(message);
    }

}
