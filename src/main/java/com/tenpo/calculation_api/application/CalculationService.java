package com.tenpo.calculation_api.application;

import com.tenpo.calculation_api.domain.model.Calculation;
import com.tenpo.calculation_api.infrastructure.exception.exceptions.ExternalServiceException;
import com.tenpo.calculation_api.infrastructure.external.services.ExternalApiService;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class CalculationService {
    public static final Logger LOGGER = LoggerFactory.getLogger(CalculationService.class);

    @Value("${get_percentage_url}")
    private String getPercentageUrl;

    @Value("${percentage_cache_key}")
    private String percentageCacheKey;


    private final ExternalApiService externalApiService;
    private static final String ERROR_MESSAGE = "No se pudo obtener el porcentaje desde el servicio externo o no hay valor en caché";
    private final RMapCache<String, Double> percentageCache;

    @Autowired
    public CalculationService(ExternalApiService externalApiService, RedissonClient redissonClient) {
        this.externalApiService = externalApiService;
        this.percentageCache = redissonClient.getMapCache("percentageCacheKey");
    }

    public BigDecimal calculateWithPercentage(BigDecimal num1, BigDecimal num2) {
        Double percentage = percentageCache.get("percentage");

        if (percentage == null) {
            try {
                percentage = Double.valueOf(externalApiService.getRequest(getPercentageUrl));
                percentageCache.put("percentage", percentage, 30, TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new ExternalServiceException(ERROR_MESSAGE, e);
            }
        }

        return new Calculation(num1, num2, percentage).calculateResult();
    }
}