package com.tenpo.calculation_api.infrastructure.interceptor;

import com.tenpo.calculation_api.infrastructure.exception.ErrorDetails;
import com.tenpo.calculation_api.infrastructure.interceptor.Wrapper.RequestWrapper;
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

@Data
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    public static final Logger LOGGER = LoggerFactory.getLogger(RateLimitInterceptor.class);
    private final ExternalCacheService externalCacheService;
    private final ApplicationEventPublisher eventPublisher;
    private static final String ERROR_MESSAGE = "Too many requests - please try again later.";


    public RateLimitInterceptor(ExternalCacheService externalCacheService, ApplicationEventPublisher eventPublisher) {
        this.externalCacheService = externalCacheService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        RRateLimiter rateLimiter = externalCacheService.getRrateLimiter();

        RequestWrapper requestWrapper = (RequestWrapper) request.getAttribute("cachedRequest");
        if (requestWrapper == null) {
            requestWrapper = new RequestWrapper(request);
            request.setAttribute("cachedRequest", requestWrapper);
        }

        if (!rateLimiter.tryAcquire()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().flush();
            ErrorDetails errorDetails = new ErrorDetails(ERROR_MESSAGE);
            request.setAttribute("errorDetails", errorDetails);
            return false;
        }
        return true;
    }

}
