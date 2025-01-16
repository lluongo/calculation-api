package com.tenpo.calculation_api.infrastructure.external.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@Service
public class ExternalApiService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ExternalApiService.class);

    private final HttpClient httpClient;

    public ExternalApiService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Retryable(
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000)
    )
    public String getRequest(String url) throws IOException, InterruptedException {

        LOGGER.info("Retry Number:{} ", Objects.requireNonNull(RetrySynchronizationManager.getContext()).getRetryCount());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}
