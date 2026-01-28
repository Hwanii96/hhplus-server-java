package kr.hhplus.be.server.point.service;

import kr.hhplus.be.server.point.entity.PointEntity;
import kr.hhplus.be.server.point.repository.PointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Transactional(readOnly = true)
    public PointEntity getPoint(long userId) {

        return pointRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("point not found"));
    }

    /** 충전: row 없으면 생성 후 충전 */
    @Transactional
    public PointEntity charge(long userId, int amount) {

        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");

        LocalDateTime now = LocalDateTime.now();

        PointEntity pointEntity = pointRepository.findById(userId)
                .orElseGet(() -> pointRepository.save(PointEntity.createZero(userId, now)));

        long newPoint = pointEntity.getPoint() + amount;

        pointEntity.changePoint(newPoint, now); // 더티 체킹

        return pointEntity;

    }

    /** 차감: row 없으면 예외 */
    @Transactional
    public PointEntity deduct(long userId, long amount) {

        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");

        LocalDateTime now = LocalDateTime.now();

        PointEntity pointEntity = pointRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("point not found"));

        long balance = pointEntity.getPoint();

        if (balance < amount) {
            throw new IllegalStateException("insufficient point");
        }

        pointEntity.changePoint(balance - amount, now); // 더티 체킹

        return pointEntity;

    }

}
