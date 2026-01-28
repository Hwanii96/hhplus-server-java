package kr.hhplus.be.server.booking.infrastructure.config;

import kr.hhplus.be.server.booking.adapter.outbound.inmemory.QueueTokenInMemoryAdapter;
import kr.hhplus.be.server.booking.adapter.outbound.inmemory.SeatLockInMemoryAdapter;
import kr.hhplus.be.server.booking.adapter.outbound.internal.PointLayeredAdapter;
import kr.hhplus.be.server.booking.adapter.outbound.internal.SeatLayeredAdapter;
import kr.hhplus.be.server.booking.adapter.outbound.persistence.adapter.PaymentPersistenceAdapter;
import kr.hhplus.be.server.booking.adapter.outbound.persistence.adapter.ReservationPersistenceAdapter;
import kr.hhplus.be.server.booking.adapter.outbound.persistence.repository.PaymentJpaRepository;
import kr.hhplus.be.server.booking.adapter.outbound.persistence.repository.ReservationJpaRepository;
import kr.hhplus.be.server.booking.application.service.PaymentInteractor;
import kr.hhplus.be.server.booking.application.service.ReservationInteractor;
import kr.hhplus.be.server.booking.domain.policy.SeatHoldPolicy;
import kr.hhplus.be.server.booking.infrastructure.inmemory.RedisInMemoryStore;
import kr.hhplus.be.server.booking.port.inbound.PaymentUseCase;
import kr.hhplus.be.server.booking.port.inbound.ReservationUseCase;
import kr.hhplus.be.server.booking.port.outbound.*;
import kr.hhplus.be.server.concert.service.SeatService;
import kr.hhplus.be.server.point.service.PointService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

@Configuration
@Profile("product")
public class BookingModuleConfig {

    // ========== 공통 Bean ==========

    @Bean
    public Supplier<Instant> nowProvider() {
        return () -> Instant.now(); // return Instant::now;
    }

    @Bean
    public SeatHoldPolicy seatHoldPolicy() {
        return new SeatHoldPolicy(Duration.ofMinutes(5));
    }

    // ========== InMemory ==========
    @Bean
    public RedisInMemoryStore RedisinMemoryStore() {
        return new RedisInMemoryStore();
    }

    @Bean
    public QueueTokenPort queueTokenPort(RedisInMemoryStore redisInMemoryStore) {
        return new QueueTokenInMemoryAdapter(redisInMemoryStore);
    }

    @Bean
    public SeatLockPort seatLockPort(RedisInMemoryStore redisInMemoryStore, Supplier<Instant> nowProvider) {
        return new SeatLockInMemoryAdapter(redisInMemoryStore, nowProvider);
    }

    // ========== DB (JPA) ==========
    @Bean
    public SeatPort seatPort(SeatService seatService) {
        return new SeatLayeredAdapter(seatService);
    }

    @Bean
    public ReservationPort reservationPort(ReservationJpaRepository reservationJpaRepository) {
        return new ReservationPersistenceAdapter(reservationJpaRepository);
    }

    @Bean
    public PointPort pointPort(PointService pointService) {
        return new PointLayeredAdapter(pointService);
    }

    @Bean
    public PaymentRepositoryPort paymentRepositoryPort(PaymentJpaRepository paymentJpaRepository) {
        return new PaymentPersistenceAdapter(paymentJpaRepository);
    }

    // ========== UseCase (Interactor) ==========
    @Bean
    public ReservationUseCase reservationUseCase
    (
            QueueTokenPort queueTokenPort,
            SeatPort seatPort,
            SeatLockPort seatLockPort,
            ReservationPort reservationPort,
            SeatHoldPolicy seatHoldPolicy,
            Supplier<Instant> nowProvider
    )
    {
        return new ReservationInteractor
                (
                        queueTokenPort,
                        seatPort,
                        seatLockPort,
                        reservationPort,
                        seatHoldPolicy,
                        nowProvider
                );
    }

    @Bean
    public PaymentUseCase paymentUseCase
            (
                    QueueTokenPort queueTokenPort,
                    SeatPort seatPort,
                    ReservationPort reservationPort,
                    PointPort pointPort,
                    PaymentRepositoryPort paymentRepositoryPort,
                    Supplier<Instant> nowProvider
            )
    {
        return new PaymentInteractor
                (
                        queueTokenPort,
                        seatPort,
                        reservationPort,
                        pointPort,
                        paymentRepositoryPort,
                        nowProvider
                );
    }

}
