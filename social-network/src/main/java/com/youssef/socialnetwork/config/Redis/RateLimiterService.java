package com.youssef.socialnetwork.config.Redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RateLimitingProperties props;

    /**
     * Sliding-window-ish (fixed window) limiter using Redis INCR + EXPIRE.
     * @param bucketKey unique key per user/ip/context
     * @param maxAttempts allowed attempts in the window
     * @param windowSeconds window length
     * @return current count after increment
     */
    public long hit(String bucketKey, int maxAttempts, int windowSeconds) {
        var ops = stringRedisTemplate.opsForValue();
        Long count = ops.increment(bucketKey);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(bucketKey, Duration.ofSeconds(windowSeconds));
        }
        return count == null ? 0 : count;
    }

    public boolean isLimited(String bucketKey, int maxAttempts) {
        String v = stringRedisTemplate.opsForValue().get(bucketKey);
        if (v == null) return false;
        try {
            return Long.parseLong(v) >= maxAttempts;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Convenience for login limiter with configured values
    public long hitLogin(String bucketKey) {
        return hit(bucketKey, props.getLoginMaxAttempts(), props.getLoginWindowSeconds());
    }

    public boolean loginLimited(String bucketKey) {
        return isLimited(bucketKey, props.getLoginMaxAttempts());
    }
}
