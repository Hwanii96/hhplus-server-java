package kr.hhplus.be.server.booking.adapter.inbound.controller;

import kr.hhplus.be.server.booking.application.exception.payment.*;
import kr.hhplus.be.server.booking.application.exception.reservation.QueueTokenInactiveException;
import kr.hhplus.be.server.booking.application.exception.reservation.SeatAlreadyHeldException;
import kr.hhplus.be.server.booking.application.exception.reservation.SeatNotAvailableException;
import kr.hhplus.be.server.booking.application.exception.reservation.SeatNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(basePackages = "kr.hhplus.be.server.booking")
public class BookingExceptionHandler {

    public record ErrorResponse
            (
                    String message,
                    Instant timestamp
            )
    {

    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse badRequest(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(AccessTokenUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ErrorResponse unauthorized(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler({QueueTokenInactiveException.class, ReservationOwnerMismatchException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public ErrorResponse forbidden(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler({SeatNotFoundException.class, ReservationNotFoundException.class, PointNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse notFound(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler({SeatNotAvailableException.class, SeatAlreadyHeldException.class, ReservationStatusNotTemporaryException.class, ReservationExpiredException.class})
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse conflict(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InsufficientPointException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    public ErrorResponse unprocessable(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ErrorResponse serverError(Exception e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

}
