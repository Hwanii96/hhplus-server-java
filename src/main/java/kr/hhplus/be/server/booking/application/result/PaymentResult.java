package kr.hhplus.be.server.booking.application.result;

import kr.hhplus.be.server.booking.domain.model.enums.SeatStatus;

import java.time.Instant;

// 최소로 필요한 필드를 작성하고 이후에 추가적으로 필요한 필드가 있을 경우 작성
public record PaymentResult
        (
                long paymentId,
                SeatStatus seatStatus,
                long paidAmount,
                long userCurrentPoints,
                Instant paidAt
        )
{
}
