package com.tenpo.calculation_api.infrastructure.external.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ExternalApiService {

    private final HttpClient httpClient;

    public ExternalApiService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getRequest(String url) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        validateResponse(response);
        return response.body();
    }

    private void validateResponse(HttpResponse<?> response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error HTTP: " + response.statusCode() + " - " + response.body());
        }
    }

    public Double getPercentage() {
        // Mock: Retorna un 10%
        return 10.0;
    }

}
