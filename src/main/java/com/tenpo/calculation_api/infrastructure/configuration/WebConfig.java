package com.tenpo.calculation_api.infrastructure.configuration;


import com.tenpo.calculation_api.infrastructure.interceptor.RateLimitInterceptor;
import com.tenpo.calculation_api.infrastructure.tool.ResponseCaptureFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/**");
    }


    @Bean
    public FilterRegistrationBean<ResponseCaptureFilter> responseCaptureFilter(ApplicationEventPublisher eventPublisher) {
        FilterRegistrationBean<ResponseCaptureFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ResponseCaptureFilter(eventPublisher));
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
