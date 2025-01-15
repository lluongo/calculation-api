package com.tenpo.calculation_api.infrastructure.external.httpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Bean
    public static HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
}
