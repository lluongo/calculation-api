package com.tenpo.calculation_api.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CalculationResponse {
    private BigDecimal result;
    private BigDecimal num1;
    private BigDecimal num2;
}
