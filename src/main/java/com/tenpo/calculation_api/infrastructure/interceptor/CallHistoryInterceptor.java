package com.tenpo.calculation_api.infrastructure.interceptor;

import com.tenpo.calculation_api.infrastructure.event.CallHistoryEvent;
import com.tenpo.calculation_api.infrastructure.exception.ErrorDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CallHistoryInterceptor implements HandlerInterceptor {
    public static final Logger LOGGER = LoggerFactory.getLogger(CallHistoryInterceptor.class);
    private final ApplicationEventPublisher eventPublisher;

    public CallHistoryInterceptor(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {


        String endpoint = request.getRequestURI();
        String parameters = request.getQueryString();
        String responseContent = response.getStatus() == 200 ? "success" : null;
        String errorMessage = null;

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            ErrorDetails errorDetails = (ErrorDetails) attr.getRequest().getAttribute("errorDetails");
            if (errorDetails != null) {
                errorMessage = response.getStatus() + "-" + errorDetails.getMessage();
                System.out.println("Error capturado: " + errorDetails.getMessage());
            }
        }
        eventPublisher.publishEvent(new CallHistoryEvent(endpoint, parameters, responseContent, errorMessage, LocalDateTime.now()));
    }
}