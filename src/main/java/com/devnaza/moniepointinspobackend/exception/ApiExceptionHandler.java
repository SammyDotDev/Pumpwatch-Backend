package com.devnaza.moniepointinspobackend.exception;

import com.devnaza.moniepointinspobackend.dto.errorResponse.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception){
        Map<String, String> fieldErrors = exception .getBindingResult()
                                                  .getFieldErrors()
                                                  .stream()
                                                  .collect(Collectors.toMap(
                                                          FieldError::getField,
                                                          FieldError::getDefaultMessage,
                                                          (existing, replacement) -> existing + "; " + replacement // merge duplicates if any
                                                  ));

        ErrorResponse error = ErrorResponse.builder()
                                      .message("Validation failed")
                                      .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                      .statusCode(HttpStatus.BAD_REQUEST.value())
                                      .fieldErrors(fieldErrors)
                                      .timestamp(System.currentTimeMillis())
                                      .build();

        return ResponseEntity.badRequest().body(error);

    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExists(UserAlreadyExistsException exception){
        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, String>> handleSQLException(SQLException exception){
        Map<String, String> error = new HashMap<>();
        error.put("error", exception.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException exception){
        Map<String, String> errors = new HashMap<>();

        errors.put("error", exception.getMessage());

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPasswordException(InvalidPasswordException exception){
        Map<String, String> errors = new HashMap<>();

        errors.put("error", exception.getMessage());

        return ResponseEntity.badRequest().body(errors);
    }
}
