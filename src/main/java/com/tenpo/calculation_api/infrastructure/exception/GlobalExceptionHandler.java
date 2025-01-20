package com.tenpo.calculation_api.infrastructure.exception;

import com.tenpo.calculation_api.infrastructure.exception.exceptions.ExternalServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.postgresql.util.PSQLException;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        LOGGER.info(ex.getMessage());
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage
                ));

        ErrorDetails errorDetails = new ErrorDetails(errors,"Validation Error");
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        LOGGER.info(ex.getMessage());
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        ErrorDetails errorDetails = new ErrorDetails(errors,"Validation Error");
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorDetails> handleExternalServiceException(ExternalServiceException ex, WebRequest request) {
        LOGGER.info(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("Error",ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(errors,"External Service Error");
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(RedisException.class)
    public ResponseEntity<?> handleRedisGlobalException(RedisException ex, WebRequest request) {
        LOGGER.info(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("Error","There is a problem with the redis server");
        ErrorDetails errorDetails = new ErrorDetails(errors,"Internal Redis Error");
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ErrorDetails> handleExternalServiceException(PSQLException ex, WebRequest request) {
        LOGGER.info(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("Error",ex.getMessage().substring(0, 254));
        ErrorDetails errorDetails = new ErrorDetails(errors,"External Service Error");
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        LOGGER.info(ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("Error","Server Error");
        ErrorDetails errorDetails = new ErrorDetails(errors,"Internal Server Error");
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}