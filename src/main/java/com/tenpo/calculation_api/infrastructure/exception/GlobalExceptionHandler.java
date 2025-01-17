package com.tenpo.calculation_api.infrastructure.exception;

import com.tenpo.calculation_api.infrastructure.exception.exceptions.ExternalServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.redisson.client.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage
                ));
        ErrorDetails errorDetails = new ErrorDetails(errors.toString(), request.getDescription(false));
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
//
//        switch (ex.getMessage()) {
//            case String m when m.contains("num2 is marked non-null but is null") -> {
//                return handleNoNullException(new NoNullException("Num2 is null", ex.getCause()), request);
//            }
//            case String m when m.contains("num1 is marked non-null but is null") -> {
//                return handleNoNullException(new NoNullException("Num1 is null", ex.getCause()), request);
//            }
//            case String m when m.contains("Unexpected character ('}' (code 125))") -> {
//                return handleNoNullException(new NoNullException("expected a value", ex.getCause()), request);
//            }
//            case String m when m.contains("Cannot deserialize value of type `java.math.BigDecimal`") -> {
//                return handleCustomNullParameterException(new NullParameterException("Some parameter is not a valid numerical representation", ex.getCause()), request);
//            }
//            case String m when m.contains("Required request body is missing") -> {
//                return handleCustomNullParameterException(new NullParameterException("Required request body is missing", ex.getCause()), request);
//            }
//            default -> {
//                return handleGlobalException(ex, request);
//            }
//        }
//    }
//
//
//    @ExceptionHandler(NoNullException.class)
//    public ResponseEntity<?> handleNoNullException(NoNullException ex, WebRequest request) {
//        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
//        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
//        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(NullParameterException.class)
//    public ResponseEntity<?> handleCustomNullParameterException(NullParameterException ex, WebRequest request) {
//        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
//        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
//        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorDetails> handleExternalServiceException(ExternalServiceException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage(), request.getDescription(false));
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(RedisException.class)
    public ResponseEntity<?> handleRedisGlobalException(RedisException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("Internal Redis Error", request.getDescription(false));
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        LOGGER.info("handleGlobalException: " + ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails("Internal Server Error", request.getDescription(false));
        request.setAttribute("errorDetails", errorDetails, WebRequest.SCOPE_REQUEST);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}