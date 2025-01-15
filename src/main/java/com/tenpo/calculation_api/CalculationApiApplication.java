package com.tenpo.calculation_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class CalculationApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculationApiApplication.class, args);
    }

}
