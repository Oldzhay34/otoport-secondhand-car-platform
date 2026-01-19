package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        ApiErrorResponse err = new ApiErrorResponse();
        err.setStatus(404);
        err.setError("Not Found");
        err.setMessage("Resource not found");
        err.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        ApiErrorResponse err = new ApiErrorResponse();
        err.setStatus(400);
        err.setError("Bad Request");
        err.setMessage(ex.getMessage());
        err.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(IllegalStateException ex, HttpServletRequest req) {
        ApiErrorResponse err = new ApiErrorResponse();
        err.setStatus(409);
        err.setError("Conflict");
        err.setMessage(ex.getMessage());
        err.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ApiErrorResponse err = new ApiErrorResponse();
        err.setStatus(400);
        err.setError("Validation Error");
        err.setMessage("Validation failed");
        err.setPath(req.getRequestURI());

        err.setFieldErrors(
                ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new ApiErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.toList())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(Exception ex, HttpServletRequest req) {
        ex.printStackTrace(); // ✅ dev için, sonra log.error yaparsın

        ApiErrorResponse err = new ApiErrorResponse();
        err.setStatus(500);
        err.setError("Internal Server Error");
        err.setMessage("Unexpected error");
        err.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

}
