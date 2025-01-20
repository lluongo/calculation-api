package com.tenpo.calculation_api.infrastructure.redis;

import jakarta.annotation.PreDestroy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {

    private RedissonClient redissonClient;

    @Value("${redisson_config_yml}")
    private String redissonYml;

    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(getClass().getClassLoader().getResource(redissonYml));
        config.setThreads(16);
        config.setNettyThreads(32);
        this.redissonClient = Redisson.create(config);
        return this.redissonClient;
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        return new RedissonSpringCacheManager(redissonClient);
    }

    @PreDestroy
    public void shutdownRedisson() {
        if (this.redissonClient != null && !this.redissonClient.isShutdown()) {
            this.redissonClient.shutdown();
        }
    }

}
