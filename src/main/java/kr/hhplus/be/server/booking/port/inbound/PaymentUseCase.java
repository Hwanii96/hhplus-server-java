package kr.hhplus.be.server.booking.port.inbound;

import kr.hhplus.be.server.booking.application.command.PaymentCommand;
import kr.hhplus.be.server.booking.application.result.PaymentResult;

public interface PaymentUseCase {

    PaymentResult pay(PaymentCommand paymentCommand);
}
