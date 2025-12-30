package kr.hhplus.be.server.booking.port.inbound;

import kr.hhplus.be.server.booking.application.command.ReservationCommand;
import kr.hhplus.be.server.booking.application.result.ReservationResult;

public interface ReservationUseCase {

    ReservationResult reserve(ReservationCommand reservationCommand);

}
