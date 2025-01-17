package com.tenpo.calculation_api.infrastructure.interceptor;

import com.tenpo.calculation_api.infrastructure.interceptor.RateLimitInterceptor;
import com.tenpo.calculation_api.infrastructure.interceptor.Wrapper.RequestWrapper;
import com.tenpo.calculation_api.infrastructure.redis.RedissonConfig;
import com.tenpo.calculation_api.infrastructure.redis.service.ExternalCacheService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RRateLimiter;
import org.springframework.context.ApplicationEventPublisher;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateLimitInterceptorTest {

    @Mock
    private ExternalCacheService externalCacheService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RedissonConfig redissonConfig;

    @InjectMocks
    private RateLimitInterceptor rateLimitInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RRateLimiter rateLimiter;

    @Mock
    private RequestWrapper requestWrapper;

    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        when(request.getAttribute("cachedRequest")).thenReturn(requestWrapper);
        when(response.getWriter()).thenReturn(printWriter);
        when(externalCacheService.getRrateLimiter()).thenReturn(rateLimiter);
    }

//    @Test
//    void testPreHandle_RateLimited() throws Exception {
//        when(rateLimiter.tryAcquire()).thenReturn(false);
//
//        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());
//
//        verify(redissonConfig, times(1)).verifyAndInitializeRateLimiter();
//        verify(response, times(1)).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//        verify(printWriter, times(1)).flush();
//        assertFalse(result);
//        assertEquals("Too many requests - please try again later.", ((ErrorDetails) request.getAttribute("errorDetails")).getDetails().get("Error"));
//    }

//    @Test
//    void testPreHandle_NotRateLimited() throws Exception {
//        when(rateLimiter.tryAcquire()).thenReturn(true);
//
//        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());
//
//        verify(redissonConfig, times(1)).verifyAndInitializeRateLimiter();
//        assertEquals(true, result);
//    }
//
//    @Test
//    void testPreHandle_RequestWrapperNotCached() throws Exception {
//        when(request.getAttribute("cachedRequest")).thenReturn(null);
//        when(rateLimiter.tryAcquire()).thenReturn(true);
//
//        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());
//
//        verify(request, times(1)).setAttribute(eq("cachedRequest"), any(RequestWrapper.class));
//        assertEquals(true, result);
//    }
}

