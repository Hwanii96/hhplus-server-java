package kr.hhplus.be.server.booking.adapter.outbound.internal;

/**
 * internal : 
 * -> Reservation UseCase (Interactor) 가 필요로 하는 기능을 서비스 내부의 다른 모듈 (Point 등) 로 위임하여 구현하는 Adapter를 모아두기 위함
 * 
 * PointLayeredAdapter :
 * -> Reservation UseCase (Interactor) 가 Point 저장소에 직접 접근하지 않고, Point 모듈의 서비스 (예 : PointService) 를 호출하여 기능을 수행
 */
public class PointLayeredAdapter {
    
}