package com.tenpo.calculation_api.presentation.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class CalculationRequest {
    @NotNull(message = "El numero 1 no puede ser nulo")
    private BigDecimal num1;

    @NotNull(message = "El numero 2 no puede ser nulo")
    private BigDecimal num2;
}