package com.devnaza.pumpwatch.exception;

import com.devnaza.pumpwatch.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
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
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException exception){


        ErrorResponse errorResponse =
                ErrorResponse.builder().message(exception.getMessage()).status(HttpStatus.BAD_REQUEST.getReasonPhrase()).statusCode(HttpStatus.BAD_REQUEST.value()).timestamp(System.currentTimeMillis()).build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                                                   .message(exception.getMessage())
                                                   .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                                   .statusCode(HttpStatus.BAD_REQUEST.value())
                                                   .timestamp(System.currentTimeMillis())
                                                   .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                                                   .message(exception.getMessage())
                                                   .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                                   .statusCode(HttpStatus.BAD_REQUEST.value())
                                                   .timestamp(System.currentTimeMillis())
                                                   .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                                                   .message(exception.getMessage())
                                                   .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                                   .statusCode(HttpStatus.BAD_REQUEST.value())
                                                   .timestamp(System.currentTimeMillis())
                                                   .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotFoundException(EmailNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                                              .message(exception.getMessage())
                                              .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                              .statusCode(HttpStatus.BAD_REQUEST.value())
                                              .timestamp(System.currentTimeMillis())
                                              .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception){

        String message = resolveConstraintMessage(exception);


        ErrorResponse errorResponse = ErrorResponse.builder()
                                              .message(message)
                                              .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                              .statusCode(HttpStatus.BAD_REQUEST.value())
                                              .timestamp(System.currentTimeMillis())
                                              .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    private String resolveConstraintMessage(DataIntegrityViolationException ex) {
        String cause = ex.getMostSpecificCause().getMessage();

        if (cause == null) return "A data conflict occurred";

        // ── Map known constraint names to human-readable messages ────
        if (cause.contains("uk_users_email") || cause.contains("(email)")) {
            return "An account with this email address already exists";
        }
        if (cause.contains("uk_users_phone_number") || cause.contains("(phone_number)")) {
            return "An account with this phone number already exists";
        }
        if (cause.contains("uk_users_username") || cause.contains("(username)")) {
            return "This username is already taken";
        }

        // Fallback — readable but not exposing raw DB details
        return "A record with this information already exists";
    }
}
