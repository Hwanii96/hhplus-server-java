package kr.hhplus.be.server.point.controller;

import kr.hhplus.be.server.point.exception.InsufficientException;
import kr.hhplus.be.server.point.exception.NotFoundException;
import kr.hhplus.be.server.point.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice(basePackages = "kr.hhplus.be.server.point")
public class PointExceptionHandler {

    public record ErrorResponse
            (
                    String message, Instant timestamp
            )
    {

    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ErrorResponse unauthorized(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse notFound(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InsufficientException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse conflict(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse badRequest(RuntimeException e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ErrorResponse serverError(Exception e) {
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

}
