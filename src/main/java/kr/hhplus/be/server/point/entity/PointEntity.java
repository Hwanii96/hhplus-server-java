package kr.hhplus.be.server.point.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users_points")
public class PointEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "point", nullable = false)
    private long point;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected PointEntity() {

    }   // JPA 기본 생성자

    private PointEntity(Long userId, int point, LocalDateTime updatedAt) {
        this.userId = userId;
        this.point = point;
        this.updatedAt = updatedAt;
    }

    /** 유저 포인트 row 최초 생성 (정책 : 없으면 0으로 생성) */
    public static PointEntity createZero(Long userId, LocalDateTime now) {
        return new PointEntity(userId, 0, now);
    }

    /** 포인트 변경 (차감/충전 공용) */
    public void changePoint(long newPoint, LocalDateTime now) {
        this.point = newPoint;
        this.updatedAt = now;
    }

    public Long getUserId() { return userId; }

    public long getPoint() { return point; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

}
