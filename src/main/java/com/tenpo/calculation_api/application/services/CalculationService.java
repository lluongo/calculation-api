package com.tenpo.calculation_api.application.services;

import com.tenpo.calculation_api.domain.model.Calculation;
import com.tenpo.calculation_api.infrastructure.external.services.ExternalApiService;
import com.tenpo.calculation_api.infrastructure.redis.service.ExternalCacheService;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalculationService {
    public static final Logger LOGGER = LoggerFactory.getLogger(CalculationService.class);
    private final ExternalApiService externalApiService;
    private final ExternalCacheService externalCacheService;

    @Autowired
    public CalculationService(ExternalCacheService externalCacheService, ExternalApiService externalApiService, RedissonClient redissonClient) {
        this.externalApiService = externalApiService;
        this.externalCacheService = externalCacheService;
    }

    public BigDecimal calculateWithPercentage(BigDecimal num1, BigDecimal num2) {

        Double percentage = externalCacheService.getPercentage("percentage");

        if (percentage == null) {
            try {
                String url = "http://localhost:8091/percentage";
                percentage = Double.valueOf(externalApiService.getRequest(url));
                externalCacheService.putPercentage("percentage", percentage);
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
                throw new IllegalStateException("No se pudo obtener el porcentaje desde el servicio externo, y no hay valor en caché.");
            }
        }

        return new Calculation(num1, num2, percentage).calculateResult();
    }
}