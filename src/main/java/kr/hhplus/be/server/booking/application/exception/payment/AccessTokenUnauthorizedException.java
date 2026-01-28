package kr.hhplus.be.server.booking.application.exception.payment;

public class AccessTokenUnauthorizedException extends RuntimeException {

    public AccessTokenUnauthorizedException(String message) {
        super(message);
    }

}
