package com.tenpo.calculation_api.infrastructure.redis;

import jakarta.annotation.PreDestroy;
import org.apache.catalina.util.RateLimiter;
import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class RedissonConfig {

    private RedissonClient redissonClient;
    private static final String RATE_LIMITER_KEY = "apiRateLimiter";

    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(getClass().getClassLoader().getResource("redisson.yml"));
        this.redissonClient = Redisson.create(config);
        return this.redissonClient;
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        return new RedissonSpringCacheManager(redissonClient);
    }

    private void initializeRateLimiter() {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(RATE_LIMITER_KEY);
        rateLimiter.trySetRate(RateType.OVERALL, 3, Duration.ofMinutes(1));
    }

    @PreDestroy
    public void shutdownRedisson() {
        if (this.redissonClient != null && !this.redissonClient.isShutdown()) {
            this.redissonClient.shutdown();
        }
    }

    public void verifyAndInitializeRateLimiter() {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(RATE_LIMITER_KEY);
        if (!rateLimiter.isExists()) {
            initializeRateLimiter();
        }
    }

}
