package com.tenpo.calculation_api.application;

import com.tenpo.calculation_api.domain.model.Calculation;
import com.tenpo.calculation_api.infrastructure.exception.exceptions.ExternalServiceException;
import com.tenpo.calculation_api.infrastructure.external.services.ExternalApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculationServiceTest {

    @Mock
    private ExternalApiService externalApiService;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RMapCache percentageCache;

    private CalculationService calculationService;

    @Value("${get_percentage_url}")
    private String getPercentageUrl;

    @Value("${percentage_cache_key}")
    private String percentageCacheKey;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simula el RMapCache con los tipos genéricos explícitos
        percentageCache = mock(RMapCache.class);
        when(redissonClient.getMapCache("percentageCacheKey")).thenReturn(percentageCache);

        calculationService = new CalculationService(externalApiService, redissonClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe calcular el resultado utilizando el porcentaje desde la caché")
    void testCalculateWithPercentage_ValidPercentageFromCache() throws IOException, InterruptedException {
        // Arrange
        BigDecimal num1 = BigDecimal.valueOf(100);
        BigDecimal num2 = BigDecimal.valueOf(200);
        Double percentage = 10.0;

        when(percentageCache.get("percentage")).thenReturn(percentage);

        // Act
        BigDecimal result = calculationService.calculateWithPercentage(num1, num2);

        // Assert
        assertNotNull(result);
        assertEquals(new Calculation(num1, num2, percentage).calculateResult(), result);
        verify(percentageCache, never()).put(anyString(), anyDouble(), anyLong(), any(TimeUnit.class));
        verify(externalApiService, never()).getRequest(getPercentageUrl);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe calcular el resultado utilizando el porcentaje desde el servicio externo y guardar en caché")
    void testCalculateWithPercentage_ValidPercentageFromExternalApi() throws IOException, InterruptedException {
        // Arrange
        BigDecimal num1 = BigDecimal.valueOf(100);
        BigDecimal num2 = BigDecimal.valueOf(200);
        Double percentage = 15.0;

        when(percentageCache.get("percentage")).thenReturn(null);
        when(externalApiService.getRequest(getPercentageUrl)).thenReturn(percentage.toString());

        // Act
        BigDecimal result = calculationService.calculateWithPercentage(num1, num2);

        // Assert
        assertNotNull(result);
        assertEquals(new Calculation(num1, num2, percentage).calculateResult(), result);
        verify(percentageCache).put("percentage", percentage, 30, TimeUnit.MINUTES);
        verify(externalApiService).getRequest(getPercentageUrl);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Debe lanzar una excepción cuando no hay porcentaje en caché y falla el servicio externo")
    void testCalculateWithPercentage_ExternalApiThrowsException() throws IOException, InterruptedException {
        // Arrange
        BigDecimal num1 = BigDecimal.valueOf(100);
        BigDecimal num2 = BigDecimal.valueOf(200);

        when(percentageCache.get("percentage")).thenReturn(null);
        when(externalApiService.getRequest(getPercentageUrl)).thenThrow(new RuntimeException("External API error"));

        // Act & Assert
        ExternalServiceException exception = assertThrows(ExternalServiceException.class,
                () -> calculationService.calculateWithPercentage(num1, num2));
        assertEquals("No se pudo obtener el porcentaje desde el servicio externo o no hay valor en caché", exception.getMessage());
        verify(percentageCache, never()).put(anyString(), anyDouble(), anyLong(), any(TimeUnit.class));
    }
}