package com.project.community.globalhandler;


import com.project.community.exceptions.EmailAlreadyExistsException;
import com.project.community.exceptions.ResourceNotFoundException;
import com.project.community.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException existsException)
    {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .Error(existsException.getMessage())
                        .build()
                );
    }
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException userNotFoundException)
    {
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .Error(userNotFoundException.getMessage())
                        .build()
                );
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> resourceNotFoundException(ResourceNotFoundException resourceNotFoundException)
    {
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .Error(resourceNotFoundException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgsNotValidException(MethodArgumentNotValidException validException)
    {
        Map<String, String> validationErrors = new HashMap<>();
        validException.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .errorDescription(validationErrors)
                                .build()
                );
    }
}