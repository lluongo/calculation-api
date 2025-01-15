package com.tenpo.calculation_api.domain.model;

import java.math.BigDecimal;

public class Calculation {
    private final BigDecimal num1;
    private final BigDecimal num2;
    private final Double percentage;

    public Calculation(BigDecimal num1, BigDecimal num2, Double percentage) {
        this.num1 = num1;
        this.num2 = num2;
        this.percentage = percentage;
    }

    public BigDecimal calculateResult() {
        BigDecimal addVal = num1.add(num2);
        BigDecimal mul = BigDecimal.valueOf(percentage / 100);
        return addVal.add(mul.multiply(addVal));
    }
}
