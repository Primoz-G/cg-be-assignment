package com.example.store.exception.mapper;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.store.error.ValidationError;
import com.example.store.error.ValidationErrorResponse;

@ControllerAdvice
public class ControllerExceptionMapper {

    /**
     * Maps {@link MethodArgumentNotValidException} to a 400 bad request,
     * and a {@link ValidationErrorResponse} body containing the validation errors.
     *
     * @param e validation exception
     * @return response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final List<ValidationError> validationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponse(validationErrors));
    }

}
