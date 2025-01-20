package com.tenpo.calculation_api.infrastructure.interceptor.service;

import lombok.Data;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Data
public class RateLimiterService {

    @Value("${rate_limiter_key}")
    private String rate_limiter_key;

    @Value("${number_of_request}")
    private long numberOfRequest;

    @Value("${duration}")
    private long duration;

    private RedissonClient redissonClient;
    private RRateLimiter rRateLimiter;
    private final ReentrantLock lock = new ReentrantLock();

    public RateLimiterService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void createRateLimiter() {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(rate_limiter_key);
        rateLimiter.trySetRate(RateType.OVERALL, numberOfRequest, Duration.ofMinutes(duration));
    }

    public boolean acquirePermit() {
        ensureRateLimiterExists();
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(rate_limiter_key);
        return rateLimiter.tryAcquire();
    }

    private void ensureRateLimiterExists() {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(rate_limiter_key);
        if (!rateLimiter.isExists()) {
            createRateLimiter();
        }
    }

    public RRateLimiter getRateLimiter() {
        return redissonClient.getRateLimiter(rate_limiter_key);
    }

}
