package kr.hhplus.be.server.booking.application.exception.reservation;

public class QueueTokenInactiveException extends IllegalStateException {

    public QueueTokenInactiveException() {
        super("queueToken is not ACTIVE status");
    }

    public QueueTokenInactiveException(String message) {
        super(message);
    }

}
