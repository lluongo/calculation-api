package com.tenpo.calculation_api.infrastructure.exception.exceptions;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
