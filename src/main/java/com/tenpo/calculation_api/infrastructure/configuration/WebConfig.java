package com.tenpo.calculation_api.infrastructure.configuration;

import com.tenpo.calculation_api.infrastructure.interceptor.CallHistoryInterceptor;
import com.tenpo.calculation_api.infrastructure.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;
    @Autowired
    private CallHistoryInterceptor callHistoryInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/**");
        registry.addInterceptor(callHistoryInterceptor);
    }
}
