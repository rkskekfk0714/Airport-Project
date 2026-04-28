package com.baseball.ticketing.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * Redis를 이용한 좌석 임시 점유(Hold) 서비스.
 * 봇이 좌석 점유를 시도할 때 이 계층에서 먼저 충돌이 발생함.
 */
@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final StringRedisTemplate redisTemplate;

    @Value("${seat.hold.timeout-minutes:5}")
    private int holdTimeoutMinutes;

    private static final String HOLD_KEY_PREFIX = "seat:hold:";
    private static final String USER_HOLD_KEY_PREFIX = "user:hold:";

    /**
     * 좌석 임시 점유. SETNX 방식으로 동시 요청 중 하나만 성공.
     */
    public boolean holdSeat(Long seatId, Long userId) {
        String holdKey = HOLD_KEY_PREFIX + seatId;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(holdKey, String.valueOf(userId), Duration.ofMinutes(holdTimeoutMinutes));
        if (Boolean.TRUE.equals(success)) {
            // 유저가 점유한 좌석 목록 추적
            redisTemplate.opsForSet().add(USER_HOLD_KEY_PREFIX + userId, String.valueOf(seatId));
            redisTemplate.expire(USER_HOLD_KEY_PREFIX + userId, Duration.ofMinutes(holdTimeoutMinutes));
        }
        return Boolean.TRUE.equals(success);
    }

    /**
     * 여러 좌석을 원자적으로 점유. 하나라도 실패하면 전체 롤백.
     */
    public boolean holdSeats(List<Long> seatIds, Long userId) {
        // 먼저 모든 좌석이 사용 가능한지 확인
        for (Long seatId : seatIds) {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(HOLD_KEY_PREFIX + seatId))) {
                return false;
            }
        }
        // 실제 점유
        List<Boolean> results = seatIds.stream()
                .map(seatId -> holdSeat(seatId, userId))
                .toList();

        boolean allSuccess = results.stream().allMatch(Boolean.TRUE::equals);
        if (!allSuccess) {
            // 롤백
            releaseSeats(seatIds, userId);
            return false;
        }
        return true;
    }

    public void releaseSeat(Long seatId, Long userId) {
        String holdKey = HOLD_KEY_PREFIX + seatId;
        String holdValue = redisTemplate.opsForValue().get(holdKey);
        if (String.valueOf(userId).equals(holdValue)) {
            redisTemplate.delete(holdKey);
            redisTemplate.opsForSet().remove(USER_HOLD_KEY_PREFIX + userId, String.valueOf(seatId));
        }
    }

    public void releaseSeats(List<Long> seatIds, Long userId) {
        seatIds.forEach(seatId -> releaseSeat(seatId, userId));
    }

    public boolean isHeldByUser(Long seatId, Long userId) {
        String holdKey = HOLD_KEY_PREFIX + seatId;
        return String.valueOf(userId).equals(redisTemplate.opsForValue().get(holdKey));
    }

    public Long getHoldingUserId(Long seatId) {
        String value = redisTemplate.opsForValue().get(HOLD_KEY_PREFIX + seatId);
        return value != null ? Long.parseLong(value) : null;
    }
}
