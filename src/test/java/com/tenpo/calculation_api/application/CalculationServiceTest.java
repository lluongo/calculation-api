package com.tenpo.calculation_api.application;

import com.tenpo.calculation_api.infrastructure.exception.exceptions.ExternalServiceException;
import com.tenpo.calculation_api.infrastructure.external.services.ExternalApiService;
import com.tenpo.calculation_api.infrastructure.redis.service.ExternalCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculationServiceTest {

    @Mock
    private ExternalApiService externalApiService;

    @Mock
    private ExternalCacheService externalCacheService;

    @Mock
    private RedissonClient redissonClient;

    @InjectMocks
    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        // Inicializar mocks si es necesario
    }

    @Test
    void testCalculateWithPercentage_CacheHit() {
        // Configurar el comportamiento del mock para un hit de caché
        when(externalCacheService.getPercentage("percentage")).thenReturn(10.0);

        BigDecimal result = calculationService.calculateWithPercentage(BigDecimal.valueOf(100), BigDecimal.valueOf(200));

        assertEquals(new BigDecimal("330.0"), result);
        verify(externalCacheService, times(1)).getPercentage("percentage");
        verifyNoMoreInteractions(externalApiService);
    }

    @Test
    void testCalculateWithPercentage_Cache() throws Exception {
        // Configurar el comportamiento del mock para caché caso donde no me retorna
        // un valor la clave Redis y debo llamar a la api externa

        when(externalCacheService.getPercentage("percentage")).thenReturn(null);
        when(externalApiService.getRequest(anyString())).thenReturn("15.0");

        BigDecimal result = calculationService.calculateWithPercentage(BigDecimal.valueOf(100), BigDecimal.valueOf(200));

        assertEquals(new BigDecimal("345.00"), result);
        verify(externalCacheService, times(1)).getPercentage("percentage");
        verify(externalApiService, times(1)).getRequest(anyString());
        verify(externalCacheService, times(1)).putPercentage("percentage", 15.0);
    }

    @Test
    void testCalculateWithPercentage_ExternalServiceException() throws Exception {
        // Configurar el comportamiento del mock para que calculateWithPercentage del CalculationService maneje correctamente una excepción lanzada por el ExternalApiService
        when(externalCacheService.getPercentage("percentage")).thenReturn(null);
        when(externalApiService.getRequest(anyString())).thenThrow(new RuntimeException("Service Down"));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () ->
                calculationService.calculateWithPercentage(BigDecimal.valueOf(100), BigDecimal.valueOf(200))
        );

        assertEquals("No se pudo obtener el porcentaje desde el servicio externo o no hay valor en caché", exception.getMessage());
        verify(externalCacheService, times(1)).getPercentage("percentage");
        verify(externalApiService, times(1)).getRequest(anyString());
        verifyNoMoreInteractions(externalCacheService);
    }
}
