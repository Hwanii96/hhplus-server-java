package kr.hhplus.be.server.booking.adapter.outbound.persistence.adapter;

import kr.hhplus.be.server.booking.adapter.outbound.persistence.entity.ReservationEntity;
import kr.hhplus.be.server.booking.adapter.outbound.persistence.repository.ReservationJpaRepository;
import kr.hhplus.be.server.booking.domain.model.entity.Reservation;
import kr.hhplus.be.server.booking.domain.model.enums.ReservationStatus;
import kr.hhplus.be.server.booking.port.outbound.ReservationPort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class ReservationPersistenceAdapter implements ReservationPort {

    private final ReservationJpaRepository reservationJpaRepository;

    public ReservationPersistenceAdapter(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    @Transactional
    public Reservation reserve(Reservation reservation) {

        ReservationEntity reservationEntity = new ReservationEntity
                (
                        reservation.getUserId(),
                        reservation.getScheduleId(),
                        reservation.getSeatId(),
                        reservation.getReservationStatus(),
                        reservation.getReservationExpiresAt()
                );

        ReservationEntity reservationEntitySaved = reservationJpaRepository.save(reservationEntity);

        return reservation.withId(reservationEntitySaved.getId());

    }

    @Override
    @Transactional(readOnly = true)
    public Reservation searchByReservationId(long reservationId) {

        Optional<ReservationEntity> optionalReservationEntity = reservationJpaRepository.findById(reservationId);

        if(optionalReservationEntity.isEmpty()) {
            return null;
        }

        ReservationEntity reservationEntity = optionalReservationEntity.get();

        Reservation result =  Reservation.fromEntity
                (
                        reservationEntity.getId(),
                        reservationEntity.getUserId(),
                        reservationEntity.getScheduleId(),
                        reservationEntity.getSeatId(),
                        reservationEntity.getReservationStatus(),
                        reservationEntity.getReservationExpiresAt()
                );

        return result;

    }

    @Override
    @Transactional
    public void updateReservationStatus(long reservationId, ReservationStatus reservationStatus) {

        Optional<ReservationEntity> optionalReservationEntity = reservationJpaRepository.findById(reservationId);

        if(optionalReservationEntity.isEmpty()) {
            throw new IllegalStateException("reservation is not found");
        }

        ReservationEntity reservationEntity = optionalReservationEntity.get();

        reservationEntity.changeStatus(reservationStatus); // JPA가 자동으로 반영해준다고 했었나 ?

    }

}
