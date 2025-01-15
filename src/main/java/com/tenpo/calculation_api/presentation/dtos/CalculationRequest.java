package com.tenpo.calculation_api.presentation.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculationRequest {

    private BigDecimal num1;

    private BigDecimal num2;

}