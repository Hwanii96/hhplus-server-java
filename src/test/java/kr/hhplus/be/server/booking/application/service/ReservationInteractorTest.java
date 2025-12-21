package kr.hhplus.be.server.booking.application.service;

import kr.hhplus.be.server.booking.domain.policy.SeatHoldPolicy;
import kr.hhplus.be.server.booking.port.outbound.QueueTokenPort;
import kr.hhplus.be.server.booking.port.outbound.ReservationPort;
import kr.hhplus.be.server.booking.port.outbound.SeatLockPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class ReservationInteractorTest {

    @Mock
    ReservationPort reservationPort; // 예 : DB
    @Mock
    SeatLockPort seatLockPort; // 예 : Redis
    @Mock
    QueueTokenPort queueTokenPort; // 예 : Redis

    private ReservationInteractor reservationInteractor;

    private final SeatHoldPolicy seatHoldPolicy = new SeatHoldPolicy(Duration.ofMinutes(5));
    private final Instant now = Instant.parse("2025-12-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        reservationInteractor = new ReservationInteractor
                (

                );
    }


    public static void main(String[] args) {

        /**
         * 예약을 통해 좌석을 임시 배정 받는다 -> 백엔드가 받아야할 입력 -> concertId, scheduleId, seatId, userId, queueToken
         * 
         * [ 정책 ]
         * 1. 좌석 예약을 성공적으로 수행하기 위해서는 queueToken (대기열 토큰) 의 값이 ACTIVE 여야 한다
         * 2. 좌석 예약 시 동시성 문제가 해결되어야 한다
         * 3. 좌석 예약이 성공적으로 수행되면 해당 좌석의 상태 값이 AVAILABLE에서 TEMPORARY로 변경되어야 한다
         * 4. 좌석 예약이 성공적으로 수행되면 좌석 임시 배정 (예약) 만료 시각을 현재 시각 (now) + seatHoldPolicy (Duration) 으로 세팅되어야 하며, 해당 좌석은 다른 사용자로부터 접근이 되어서는 안된다
         */

        // System.out.println("now : " + now);
    }

}
