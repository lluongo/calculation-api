package com.tenpo.calculation_api.infrastructure.exception.exceptions;

public class NullParameterException extends RuntimeException {
    public NullParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
