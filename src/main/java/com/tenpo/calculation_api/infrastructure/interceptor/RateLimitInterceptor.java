package com.tenpo.calculation_api.infrastructure.interceptor;

import com.tenpo.calculation_api.infrastructure.exception.ErrorDetails;
import com.tenpo.calculation_api.infrastructure.redis.service.ExternalCacheService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NonNull;
import org.redisson.api.RRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerInterceptor;

@Data
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ExternalCacheService externalCacheService;

    public RateLimitInterceptor(ExternalCacheService externalCacheService) {
        this.externalCacheService = externalCacheService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        RRateLimiter rateLimiter = externalCacheService.getRrateLimiter();

        if (!rateLimiter.tryAcquire()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests - please try again later.");
            ErrorDetails errorDetails = new ErrorDetails("Too many requests", "Se pueden procesar solo 3 peticiones por minuto por favor reintente mas tarde.");
            request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
            response.getWriter().flush();
            return false;
        }
        return true;
    }
}
