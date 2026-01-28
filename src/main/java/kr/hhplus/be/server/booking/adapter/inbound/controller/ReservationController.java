package kr.hhplus.be.server.booking.adapter.inbound.controller;

import kr.hhplus.be.server.booking.adapter.inbound.controller.dto.ReservationRequest;
import kr.hhplus.be.server.booking.adapter.inbound.controller.dto.ReservationResponse;
import kr.hhplus.be.server.booking.application.command.ReservationCommand;
import kr.hhplus.be.server.booking.application.result.ReservationResult;
import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.booking.port.inbound.ReservationUseCase;
import kr.hhplus.be.server.point.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationUseCase reservationUseCase;

    public ReservationController(ReservationUseCase reservationUseCase) {
        this.reservationUseCase = reservationUseCase;
    }

    @PostMapping
    public ReservationResponse reserve
            (
                    @RequestHeader(value = "Authorization", required = false) String authorization,
                    @RequestBody ReservationRequest reservationRequest
            )
    {
        long userId = extractUserIdFromAuthorization(authorization);

        // concertId를 직접적으로 예약을 할 때 사용되는 값은 아니므로 우선 제외
        // concertId를 사용해서 검증 로직이 필요한 경우 추후 추가
        ReservationCommand reservationCommand = new ReservationCommand
                (
                        reservationRequest.queueToken(),
                        userId,
                        reservationRequest.scheduleId(),
                        reservationRequest.seatId()
                );

        ReservationResult reservationResult = reservationUseCase.reserve(reservationCommand);

        return new ReservationResponse
                (
                        reservationResult.reservationId(),
                        SeatStatus.TEMPORARY,
                        reservationResult.reservationExpiresAt()
                );
    }

    // ========== 임시 : Authorization에서 userId 뽑기 ==========
    private long extractUserIdFromAuthorization(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header is missing or invalid");
        }

        String token = authorization.substring("Bearer ".length()).trim();

        try {
            return Long.parseLong(token);
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Invalid token format (expected userId)");
        }

    }

}
