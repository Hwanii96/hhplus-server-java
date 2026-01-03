package kr.hhplus.be.server.booking.domain.model.enums;

// 좌석은 요구 사항 상 클린 아키텍처가 아니라 레거시 레이어드 아키텍처로 구현하는 것이지만 현 위치에 간편하게 작성했음
public enum SeatStatus {

    AVAILABLE,
    TEMPORARY,
    RESERVED
    ;

}
