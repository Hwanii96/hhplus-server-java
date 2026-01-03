package kr.hhplus.be.server.booking.port.outbound;


import kr.hhplus.be.server.booking.domain.model.entity.Point;

public interface PointPort {

    Point searchUserPoint(long userId);

    Point deduct(long userId, long amount); // 포인트 사용 후 사용자의 포인트 잔액 정보를 포함한 Point 객체 반환

}
