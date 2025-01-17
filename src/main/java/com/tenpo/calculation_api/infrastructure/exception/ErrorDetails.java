package com.tenpo.calculation_api.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ErrorDetails {

    private String message;
    private Map<String, String> details;

    public ErrorDetails(Map<String, String> errors, String message) {
        this.message = message;
        this.details = errors;
    }

    @Override
    public String toString() {
        return details.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", "));
    }

}
