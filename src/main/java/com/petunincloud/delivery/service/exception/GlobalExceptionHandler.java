package com.petunincloud.delivery.service.exception;

import com.petunincloud.delivery.service.users.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Logger securityLog = LoggerFactory.getLogger("security");

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {

        log.warn("Business error: {} | Path: {} | User: {}",
                ex.getMessage(),
                request.getDescription(false),
                user != null ? user.getId() : "anonymous");

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now().withNano(0),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex,
            WebRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {

        log.warn("Business error: {} | Path: {} | User: {}",
                ex.getMessage(),
                request.getDescription(false),
                user != null ? user.getId() : "anonymous");

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now().withNano(0),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            WebRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {

        log.error("Unexpected error | Path: {} | User: {}",
                request.getDescription(false),
                user != null ? user.getId() : "anonymous");

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now().withNano(0),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
