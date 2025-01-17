package com.tenpo.calculation_api.infrastructure.exception.exceptions;

public class NoNullException extends RuntimeException {
    public NoNullException(String message, Throwable cause) {
        super(message, cause);
    }
}
