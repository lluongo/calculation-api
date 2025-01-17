package com.tenpo.calculation_api.infrastructure.exception.exceptions;

public class RequireParameterException extends RuntimeException {
    public RequireParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
