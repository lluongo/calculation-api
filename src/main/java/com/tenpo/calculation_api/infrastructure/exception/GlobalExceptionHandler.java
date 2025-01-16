package com.tenpo.calculation_api.infrastructure.exception;

import com.tenpo.calculation_api.infrastructure.exception.exceptions.ExternalServiceException;
import com.tenpo.calculation_api.infrastructure.exception.exceptions.NullParameterException;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex, WebRequest request) {
        if (ex.getMessage().contains("is marked non-null but is null")) {
            return handleCustomNullException(new NullParameterException(ex.getMessage()), request);
        }
        return handleGlobalException(ex, request);
    }

    @ExceptionHandler(NullParameterException.class)
    public ResponseEntity<?> handleCustomNullException(NullParameterException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorDetails> handleExternalServiceException(ExternalServiceException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("Internal Server Error", request.getDescription(false));
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}