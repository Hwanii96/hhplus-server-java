package kr.hhplus.be.server.booking.adapter.inbound.controller;

import kr.hhplus.be.server.booking.adapter.inbound.controller.dto.PaymentRequest;
import kr.hhplus.be.server.booking.adapter.inbound.controller.dto.PaymentResponse;
import kr.hhplus.be.server.booking.application.command.PaymentCommand;
import kr.hhplus.be.server.booking.application.result.PaymentResult;
import kr.hhplus.be.server.booking.port.inbound.PaymentUseCase;
import kr.hhplus.be.server.point.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    public PaymentController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    @PostMapping
    public PaymentResponse pay
            (
                    @RequestHeader(value = "Authorization", required = false) String authorization,
                    @RequestBody PaymentRequest paymentRequest
            )
    {
        long userId = extractUserIdFromAuthorization(authorization);

        PaymentCommand paymentCommand = new PaymentCommand
                (
                        paymentRequest.queueToken(),
                        userId,
                        paymentRequest.reservationId()
                );

        PaymentResult paymentResult = paymentUseCase.pay(paymentCommand);

        return new PaymentResponse
                (
                        paymentResult.paymentId(),
                        paymentResult.seatStatus(),
                        paymentResult.paidAmount(),
                        paymentResult.userCurrentPoints(),
                        paymentResult.paidAt()
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
