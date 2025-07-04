package com.mayilyan.ipgeoinfo.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimit(RateLimitExceededException e) {
        return ResponseEntity.status(429).body(e.getMessage());
    }


    // Catch-all handler for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected server error: " + ex.getMessage());
    }


    @ExceptionHandler(InvalidIpException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIp(InvalidIpException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid input");
        error.put("details", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}

