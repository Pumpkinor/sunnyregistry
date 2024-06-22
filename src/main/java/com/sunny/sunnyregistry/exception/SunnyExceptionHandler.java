package com.sunny.sunnyregistry.exception;

import com.sunny.sunnyregistry.model.vo.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception Handler 
 */

@RestControllerAdvice
public class SunnyExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse sendErrorResponse(Exception exception){
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), exception.getMessage());
    }

}
