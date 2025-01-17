package com.tenpo.calculation_api.infrastructure.tool;

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
        LOGGER.info(responseContent);
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String endpoint = httpRequest.getRequestURI();
        String params = requestWrapper.getBody();
        String errorMessage = "";


        ErrorDetails errorDetails = (ErrorDetails) request.getAttribute("errorDetails");
        if (errorDetails != null) {
            errorMessage = httpResponse.getStatus() + " - " + errorDetails.getMessage();
        }

        eventPublisher.publishEvent(new CallHistoryEvent(endpoint, params, responseContent, errorMessage, LocalDateTime.now()));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}

