package com.mayilyan.ipgeoinfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleRateLimit(RateLimitExceededException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Too many requests");
        error.put("details", e.getMessage());

        return ResponseEntity.status(429).body(error);
    }

    @ExceptionHandler(InvalidIpException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIp(InvalidIpException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid input");
        error.put("details", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected server error: " + ex.getMessage());
    }
}

