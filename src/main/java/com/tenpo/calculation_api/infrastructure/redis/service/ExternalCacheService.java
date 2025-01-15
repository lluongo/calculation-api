package com.tenpo.calculation_api.infrastructure.redis.service;

import lombok.Data;
import org.redisson.api.RMapCache;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Data
public class ExternalCacheService {

    private final RMapCache<String, Double> percentageCache;
    private final RRateLimiter rrateLimiter;

    public ExternalCacheService(RedissonClient redissonClient) {
        this.percentageCache = redissonClient.getMapCache("percentageCache");
        this.rrateLimiter = redissonClient.getRateLimiter("apiRateLimiter");
        this.rrateLimiter.trySetRate(RateType.OVERALL, 3, Duration.ofMinutes(1));
    }

    public void putPercentage(String value, Double valToSet) {
        percentageCache.put(value, valToSet, 30, TimeUnit.MINUTES);
    }

    public Double getPercentage(String value) {
        return percentageCache.get(value);
    }


}
