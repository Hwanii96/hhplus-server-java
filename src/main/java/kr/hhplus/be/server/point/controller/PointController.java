package kr.hhplus.be.server.point.controller;

import kr.hhplus.be.server.point.controller.dto.PointResponse;
import kr.hhplus.be.server.point.entity.PointEntity;
import kr.hhplus.be.server.point.exception.UnauthorizedException;
import kr.hhplus.be.server.point.service.PointService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    // GET /points/users
    @GetMapping("/users")
    public PointResponse getMyPoint(@RequestHeader(value = "Authorization", required = false) String authorization) {

        long userId = extractUserIdFromAuthorization(authorization);

        PointEntity pointEntity = pointService.getPoint(userId);

        return new PointResponse(pointEntity.getPoint());
    }

    // ========== 임시 : Authorization에서 userId 뽑기 ==========
    private long extractUserIdFromAuthorization(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header is missing or invalid");
        }

        String token = authorization.substring("Bearer ".length()).trim();

        try {
            return Long.parseLong(token);
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Invalid token format (expected userId)");
        }

    }

}
