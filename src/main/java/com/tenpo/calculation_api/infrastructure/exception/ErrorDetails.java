package com.tenpo.calculation_api.infrastructure.exception;

import lombok.Data;
import lombok.NonNull;

@Data
public class ErrorDetails {
    @NonNull
    private String message;
    @NonNull
    private String details;

    public ErrorDetails(String message, String details) {
        this.message = message;
        this.details = details;
    }
}
