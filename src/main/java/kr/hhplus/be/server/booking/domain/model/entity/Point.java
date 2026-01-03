package kr.hhplus.be.server.booking.domain.model.entity;

public class Point {

    private final long userId;
    private final long point; // or amount

    public Point(long userId, long point) {
        this.userId = userId;
        this.point = point;
    }

    public long getUserId() {
        return userId;
    }

    public long getPoint() {
        return point;
    }
}
