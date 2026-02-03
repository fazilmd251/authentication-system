package com.security.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(OtpRestrictionException.class)
    public ResponseEntity<Map<String,String>>handleOtpRestriction(OtpRestrictionException ex){
        Map<String,String> res=new HashMap<>();
        res.put("message",ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Map<String,String>> handleAlreadyExistsException(AlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message",ex.getMessage()));
    }


}
