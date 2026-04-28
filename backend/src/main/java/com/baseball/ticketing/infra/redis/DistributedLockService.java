package com.baseball.ticketing.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 기반 분산 락 서비스.
 * 예매 생성 시 동일 좌석의 중복 예매를 방지.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "lock:seat:";
    private static final long WAIT_TIME = 3L;
    private static final long LEASE_TIME = 10L;

    public <T> T executeWithLock(Long seatId, Supplier<T> task) {
        String lockKey = LOCK_PREFIX + seatId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)) {
                throw new IllegalStateException("좌석 처리 중입니다. 잠시 후 다시 시도해 주세요.");
            }
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("좌석 처리 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void executeWithMultiLock(java.util.List<Long> seatIds, Runnable task) {
        // 데드락 방지를 위해 seatId 오름차순 정렬 후 락 획득
        java.util.List<Long> sortedIds = seatIds.stream().sorted().toList();
        RLock[] locks = sortedIds.stream()
                .map(id -> redissonClient.getLock(LOCK_PREFIX + id))
                .toArray(RLock[]::new);
        RLock multiLock = redissonClient.getMultiLock(locks);
        try {
            if (!multiLock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)) {
                throw new IllegalStateException("좌석 처리 중입니다. 잠시 후 다시 시도해 주세요.");
            }
            task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("좌석 처리 중 오류가 발생했습니다.");
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }
}
