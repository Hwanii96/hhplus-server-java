package kr.hhplus.be.server.booking.port.outbound;

import kr.hhplus.be.server.booking.domain.model.entity.Reservation;

/**
 * Outbound Port : 예약 정보 저장 및 조회
 * Method Parameter 및 Return Type : Reservation Domain
 * UseCase (Interactor) 가 예약 데이터를 저장 및 조회하기 위해 사용하는 계약 (Interface)
 * 실제 구현은 Adapter (예 : JPA 기반 PersistenceAdapter) 에서 제공되도록 한다
 */
public interface ReservationPort {

    Reservation reserve(Reservation reservation);

}