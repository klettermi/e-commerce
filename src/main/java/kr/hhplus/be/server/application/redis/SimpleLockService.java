package kr.hhplus.be.server.application.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
public class SimpleLockService {
    private static final String LOCK_PREFIX = "lock:";

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> unlockScript;

    public SimpleLockService(StringRedisTemplate redis) {
        this.redis = redis;

        // Lua 스크립트: “내 value”일 때만 삭제
        String lua =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "  return redis.call('del', KEYS[1]) " +
                        "else " +
                        "  return 0 " +
                        "end";
        this.unlockScript = new DefaultRedisScript<>(lua, Long.class);
    }

    public boolean tryLock(String key, String uuid, long ttlMs) {
        String redisKey = LOCK_PREFIX + key;
        // SETNX + PX
        return Boolean.TRUE.equals(redis.opsForValue()
                .setIfAbsent(redisKey, uuid, Duration.ofMillis(ttlMs)));
    }

    public void unlock(String key, String uuid) {
        String redisKey = LOCK_PREFIX + key;
        redis.execute(unlockScript, Collections.singletonList(redisKey), uuid);
    }
}
