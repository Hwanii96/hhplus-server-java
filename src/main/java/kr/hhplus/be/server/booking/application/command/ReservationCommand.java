package kr.hhplus.be.server.booking.application.command;

/**
 * record : 값 (데이터) 만 담기위한 클래스로, DTO (VO) 를 위해 사용될 수 있는 간결한 문법이며, 보일러 플레이트를 방지할 수 있다
 * 1) private final 필드를 자동으로 생성 (final 이므로 불변 (immutable) 이며, DTO 특성 상 command 생성 후 값이 변경되면 안되기 때문에 적합하다)
 * 2) 기본 생성자 생성
 * 3) getter 생성
 * 4) equals(), hashcode() 가 생성되므로 테스트 시 값 비교가 용이하며, toString() 또한 생성되므로 디버깅 및 로깅에 용이하다
 */
public record ReservationCommand
        (
                String queueToken,
                long userId,
                long scheduleId,
                long seatId
        )
{

}





