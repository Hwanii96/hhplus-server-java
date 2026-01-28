package kr.hhplus.be.server.booking.port.outbound;

import kr.hhplus.be.server.booking.domain.model.entity.Reservation;
import kr.hhplus.be.server.booking.domain.model.enums.ReservationStatus;

/**
 * Outbound Port :
 * 1) 예약 정보 저장 및 조회 (ReservationInteractor)
 * 2) 예약 정보 업데이트 및 조회 (정말로 유효한 예약인지, 해당 사용자의 임시 배정 건인지, 해당 예약의 상태가 "TEMPORARY" 인지) (PaymentInteractor)
 * Method Parameter 및 Return Type : Reservation Domain
 * UseCase (Interactor) 가 예약 데이터를 저장 및 조회 or 업데이트 하기 위해 사용하는 계약 (Interface)
 * 실제 구현은 Adapter (예 : JPA 기반 PersistenceAdapter) 에서 제공되도록 한다
 */
public interface ReservationPort {

    Reservation reserve(Reservation reservation);

    Reservation searchByReservationId(long reservationId);

    void updateReservationStatus(long reservationId, ReservationStatus reservationStatus); // 예 : 결제 (Payment) 성공 시 좌석 예약 상태를 "RESERVED" 로 변경

}