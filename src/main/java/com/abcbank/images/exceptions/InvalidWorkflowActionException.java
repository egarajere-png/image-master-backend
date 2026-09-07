package com.abcbank.images.exceptions;

public class InvalidWorkflowActionException extends RuntimeException {

    public InvalidWorkflowActionException(String message) {
        super(message);
    }

    public InvalidWorkflowActionException(String message, Throwable cause) {
        super(message, cause);
    }
}