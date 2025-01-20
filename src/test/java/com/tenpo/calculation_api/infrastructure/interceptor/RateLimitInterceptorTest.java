package com.tenpo.calculation_api.infrastructure.interceptor;

import com.tenpo.calculation_api.infrastructure.interceptor.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RateLimitInterceptorTest {
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private RateLimiterService rateLimiterService;
    @InjectMocks
    private RateLimitInterceptor rateLimitInterceptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(rateLimitInterceptor, "error429Message", "Too many requests");
    }


    @Test
    @DisplayName("Debe retornar HTTP 429 cuando se excede el límite de tasa")
    public void whenRateLimitExceeded_thenReturnTooManyRequests() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(rateLimiterService.acquirePermit()).thenReturn(false);
        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());
        assertFalse(result);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.getStatus());
        verify(rateLimiterService, times(1)).acquirePermit();
    }

    @Test
    @DisplayName("Debe permitir la solicitud cuando no se excede el límite de tasa")
    public void whenRateLimitNotExceeded_thenProceed() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(rateLimiterService.acquirePermit()).thenReturn(true);
        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());
        assertTrue(result);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(rateLimiterService, times(1)).acquirePermit();
    }
}