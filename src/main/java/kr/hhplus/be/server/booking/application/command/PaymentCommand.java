package kr.hhplus.be.server.booking.application.command;

public record PaymentCommand
        (
                String queueToken,
                long userId,
                long reservationId
        )
{

}
