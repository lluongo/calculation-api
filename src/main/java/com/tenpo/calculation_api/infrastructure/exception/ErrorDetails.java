package com.tenpo.calculation_api.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorDetails {

    private String message;
    private String details;


    public ErrorDetails(String errorMessage) {
        this.message = errorMessage;
    }
}
