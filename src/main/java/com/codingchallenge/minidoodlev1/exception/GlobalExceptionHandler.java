package com.codingchallenge.minidoodlev1.exception;

import com.codingchallenge.minidoodlev1.data.dto.ErrorDetailsResponse;
import com.codingchallenge.minidoodlev1.data.dto.ValidationError;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Stream<ValidationError> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
        Stream<ValidationError> globalErrors = exception.getBindingResult().getGlobalErrors().stream()
                .map(globalError -> new ValidationError(globalError.getObjectName(), globalError.getDefaultMessage()));
        List<ValidationError> validationErrors = Stream.concat(fieldErrors, globalErrors).toList();
        ErrorDetailsResponse errorResponse = new ErrorDetailsResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for one or more fields.",
                request.getDescription(false).substring(4),
                validationErrors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AvailabilityConflictException.class)
    public ResponseEntity<ErrorDetailsResponse> handleAvailabilityConflict(AvailabilityConflictException exception, WebRequest request) {
        ErrorDetailsResponse response = new ErrorDetailsResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).substring(4),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetailsResponse> handleResourceNotFound(ResourceNotFoundException exception, WebRequest request) {
        ErrorDetailsResponse response = new ErrorDetailsResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false).substring(4),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
