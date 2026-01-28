package kr.hhplus.be.server.booking.adapter.outbound.internal;

import kr.hhplus.be.server.booking.domain.model.entity.Point;
import kr.hhplus.be.server.booking.port.outbound.PointPort;
import kr.hhplus.be.server.point.entity.PointEntity;
import kr.hhplus.be.server.point.service.PointService;
import org.springframework.transaction.annotation.Transactional;

/**
 * internal :
 * -> Reservation UseCase (Interactor) 가 필요로 하는 기능을 서비스 내부의 다른 모듈 (Point 등) 로 위임하여 구현하는 Adapter를 모아두기 위함
 *
 * PointLayeredAdapter :
 * -> Reservation UseCase (Interactor) 가 Point 저장소에 직접 접근하지 않고, Point 모듈의 서비스 (예 : PointService) 를 호출하여 기능을 수행
 */
public class PointLayeredAdapter implements PointPort {

    private final PointService pointService;

    public PointLayeredAdapter(PointService pointService) {
        this.pointService = pointService;
    }

    @Override
    @Transactional(readOnly = true)
    public Point searchUserPoint(long userId) {

        PointEntity pointEntity = pointService.getPoint(userId);
        // PointEntity: PK가 userId이고 잔액은 getPoint()

        return Point.fromEntity(
                pointEntity.getUserId(),
                pointEntity.getPoint()
        );
    }

    @Override
    @Transactional
    public Point deduct(long userId, long point) {

        PointEntity updated = pointService.deduct(userId, point);

        return Point.fromEntity(
                updated.getUserId(),
                updated.getPoint()
        );

    }

}
