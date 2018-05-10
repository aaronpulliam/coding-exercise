package com.intuit.cg.backendtechassessment.service.exception;

public class OperationNotPermittedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperationNotPermittedException(String message) {
        super(message);
    }

}