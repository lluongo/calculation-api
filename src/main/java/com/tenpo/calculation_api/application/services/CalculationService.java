package com.tenpo.calculation_api.application.services;

import com.tenpo.calculation_api.domain.model.Calculation;
import com.tenpo.calculation_api.infrastructure.external.httpClient.HttpClientConfig;
import com.tenpo.calculation_api.infrastructure.external.services.ExternalApiService;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.concurrent.TimeUnit;

@Service
public class CalculationService {
    private final ExternalApiService externalApiService;
    private final RMapCache<String, Double> percentageCache;
    private final String url = "http://localhost:8091/percentage";

    @Autowired
    public CalculationService(ExternalApiService externalApiService, RedissonClient redissonClient) {
        this.externalApiService = externalApiService;
        this.percentageCache = redissonClient.getMapCache("percentageCache");
    }

    public BigDecimal calculateWithPercentage(BigDecimal num1, BigDecimal num2) {

        Double percentage = percentageCache.get("percentage");

        if (percentage == null) {
            try {
                HttpClient httpClient = HttpClientConfig.createHttpClient();
                ExternalApiService externalApiService = new ExternalApiService(httpClient);
                percentage = Double.valueOf(externalApiService.getRequest(url));
                //percentage = getPercentageFromExternalService();
                percentageCache.put("percentage", percentage, 30, TimeUnit.MINUTES);
            } catch (Exception e) {
                System.out.println("puffff" + e);
                throw new IllegalStateException("No se pudo obtener el porcentaje, y no hay valor en caché.");
            }
        }

        return new Calculation(num1, num2, percentage).calculateResult();
    }

    private Double getPercentageFromExternalService() throws Exception {
        return externalApiService.getPercentage();
    }

}