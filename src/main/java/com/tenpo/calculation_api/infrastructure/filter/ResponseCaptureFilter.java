package com.tenpo.calculation_api.infrastructure.filter;

import com.tenpo.calculation_api.infrastructure.event.CallHistoryEvent;
import com.tenpo.calculation_api.infrastructure.exception.ErrorDetails;
import com.tenpo.calculation_api.infrastructure.interceptor.Wrapper.RequestWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class ResponseCaptureFilter implements Filter {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResponseCaptureFilter.class);
    private final ApplicationEventPublisher eventPublisher;

    public ResponseCaptureFilter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request);

        chain.doFilter(requestWrapper, responseCopier);
        responseCopier.flushBuffer(); // Enviar respuesta original al cliente

        byte[] copy = responseCopier.getCopy();
        String responseContent = new String(copy, response.getCharacterEncoding());
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String endpoint = httpRequest.getMethod() + ": " + httpRequest.getRequestURI();
        String params = requestWrapper.getBody();
        String errorMessage = "";

        ErrorDetails errorDetails = (ErrorDetails) request.getAttribute("errorDetails");
        if (errorDetails != null) {
            if ( ((HttpServletResponse) response).getStatus() == 429 ){
                responseContent = errorDetails.getDetails().get("Error");
            }
            errorMessage = httpResponse.getStatus() + " - " + errorDetails.getMessage();
        }

        eventPublisher.publishEvent(new CallHistoryEvent(endpoint, params, cut(responseContent), errorMessage, LocalDateTime.now()));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    public String cut(String string) {
        return string.length() > 255 ? string.substring(0, 255) : string;
    }
}

