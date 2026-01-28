package kr.hhplus.be.server.booking.adapter.outbound.stub;

import kr.hhplus.be.server.booking.port.outbound.QueueTokenPort;

public class QueueTokenStubAdapter implements QueueTokenPort {

    @Override
    public boolean isActive(String queueToken, long userId, long scheduleId) {
        return false;
    }

    @Override
    public void expire(String queueToken) {

    }

}
