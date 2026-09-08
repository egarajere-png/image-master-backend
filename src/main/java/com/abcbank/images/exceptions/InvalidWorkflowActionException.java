package com.abcbank.images.exceptions;

/** Thrown when a requested workflow action doesn't make sense for the item's current state 
 * (e.g. no such action wired to its current queue) — maps to HTTP 400. */

public class InvalidWorkflowActionException extends RuntimeException {

    public InvalidWorkflowActionException(String message) {
        super(message);
    }

    public InvalidWorkflowActionException(String message, Throwable cause) {
        super(message, cause);
    }
}