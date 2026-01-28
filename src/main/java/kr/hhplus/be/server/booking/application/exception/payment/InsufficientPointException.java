package kr.hhplus.be.server.booking.application.exception.payment;

public class InsufficientPointException extends RuntimeException {

    public InsufficientPointException(String message) {
        super(message);
    }

}
