package com.example.SocialNetwork.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.SocialNetwork.domain.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(IllegalArgumentException illegalArgumentException) {
        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT,
                LocalDateTime.now(),
                "Illegal argument exception!",
                illegalArgumentException.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<Object> handleException(BadCredentialsException badCredentialsException) {
        return new ResponseEntity<>(new ApiError(HttpStatus.UNAUTHORIZED,
                LocalDateTime.now(),
                "Bad login attempt!",
                badCredentialsException.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = JWTVerificationException.class)
    public ResponseEntity<Object> handleException(JWTVerificationException jwtVerificationException) {
        return new ResponseEntity<>(new ApiError(HttpStatus.UNAUTHORIZED,
                LocalDateTime.now(),
                "Bad access token!",
                jwtVerificationException.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Object> handleConstraintViolationExceptions(
            MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                "Bad data sent!",
                errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> handleException(
            AccessDeniedException ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.FORBIDDEN,
                LocalDateTime.now(),
                "Access denied!",
                ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleException(
            Exception ex) {
        return new ResponseEntity<>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now(),
                "Something unexpected happened!",
                ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
