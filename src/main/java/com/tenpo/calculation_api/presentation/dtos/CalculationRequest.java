package com.tenpo.calculation_api.presentation.dtos;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class CalculationRequest {
    @NonNull
    private BigDecimal num1;
    @NonNull
    private BigDecimal num2;

}