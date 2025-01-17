package com.tenpo.calculation_api.infrastructure.interceptor;

import com.tenpo.calculation_api.infrastructure.exception.ErrorDetails;
import com.tenpo.calculation_api.infrastructure.interceptor.Wrapper.RequestWrapper;
import com.tenpo.calculation_api.infrastructure.redis.RedissonConfig;
import com.tenpo.calculation_api.infrastructure.redis.service.ExternalCacheService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NonNull;
import org.redisson.api.RRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    public static final Logger LOGGER = LoggerFactory.getLogger(RateLimitInterceptor.class);
    private final ExternalCacheService externalCacheService;
    private final ApplicationEventPublisher eventPublisher;
    private Map<String, String> errors = new HashMap<>();
    private RedissonConfig redissonConfig;

    public RateLimitInterceptor(ExternalCacheService externalCacheService, ApplicationEventPublisher eventPublisher, RedissonConfig redissonConfig) {
        this.externalCacheService = externalCacheService;
        this.eventPublisher = eventPublisher;
        this.redissonConfig = redissonConfig;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        redissonConfig.verifyAndInitializeRateLimiter();
        RRateLimiter rateLimiter = externalCacheService.getRrateLimiter();

        RequestWrapper requestWrapper = (RequestWrapper) request.getAttribute("cachedRequest");
        if (requestWrapper == null) {
            requestWrapper = new RequestWrapper(request);
            request.setAttribute("cachedRequest", requestWrapper);
        }

        if (!rateLimiter.tryAcquire()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().flush();
            errors.put("Error","Too many requests - please try again later.");
            ErrorDetails errorDetails = new ErrorDetails(errors,"Request Error");
            request.setAttribute("errorDetails", errorDetails);
            return false;
        }
        return true;
    }

}
