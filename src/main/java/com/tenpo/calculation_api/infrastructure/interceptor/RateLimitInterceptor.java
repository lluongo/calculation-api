package com.tenpo.calculation_api.infrastructure.interceptor;

import com.tenpo.calculation_api.infrastructure.exception.ErrorDetails;
import com.tenpo.calculation_api.infrastructure.interceptor.Wrapper.RequestWrapper;
import com.tenpo.calculation_api.infrastructure.interceptor.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    public static final Logger LOGGER = LoggerFactory.getLogger(RateLimitInterceptor.class);

    @Value("${error_429_Message}")
    private String error429Message;

    private final RateLimiterService rateLimiterService;
    private final ApplicationEventPublisher eventPublisher;
    private Map<String, String> errors = new HashMap<>();


    public RateLimitInterceptor(RateLimiterService rateLimiterService, ApplicationEventPublisher eventPublisher) {
        this.rateLimiterService = rateLimiterService;
        this.eventPublisher = eventPublisher;

    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {


        RequestWrapper requestWrapper = (RequestWrapper) request.getAttribute("cachedRequest");
        if (requestWrapper == null) {
            requestWrapper = new RequestWrapper(request);
            request.setAttribute("cachedRequest", requestWrapper);
        }

        if (!rateLimiterService.acquirePermit()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().flush();
            LOGGER.info(error429Message);
            errors.put("Error", error429Message);
            ErrorDetails errorDetails = new ErrorDetails(errors, "Request Error");
            request.setAttribute("errorDetails", errorDetails);
            return false;
        }
        return true;
    }

}
