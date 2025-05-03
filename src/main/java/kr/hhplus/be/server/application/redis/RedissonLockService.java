package kr.hhplus.be.server.application.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonLockService {

    private static final String LOCK_PREFIX = "lock:";
    private final RedissonClient redissonClient;

    public boolean tryLock(String key, long leaseMs) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
        try {
            // 즉시 시도(waitTime=0), 획득 시 leaseMs 뒤 자동 해제
            return lock.tryLock(0, leaseMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void unlock(String key) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
