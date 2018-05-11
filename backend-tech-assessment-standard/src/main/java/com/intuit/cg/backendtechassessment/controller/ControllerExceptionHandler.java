package com.intuit.cg.backendtechassessment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.intuit.cg.backendtechassessment.service.exception.NotFoundException;
import com.intuit.cg.backendtechassessment.service.exception.OperationNotPermittedException;

@ControllerAdvice("com.intuit.cg.backendtechassessment.controller")
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static class ErrorDetails {
        private String message;

        public ErrorDetails() {
        }

        public ErrorDetails(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorDetails handleNotFound(Exception e) {
        return new ErrorDetails(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(OperationNotPermittedException.class)
    @ResponseBody
    public ErrorDetails handleConflict(Exception e) {
        return new ErrorDetails(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorDetails handleValidation(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = null;
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }
        return new ErrorDetails(message != null ? message : "Validation failed");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorDetails handleRegularException(Exception e) {
        logger.info("Request threw exception", e);
        return new ErrorDetails("Internal Server Error");
    }

}
