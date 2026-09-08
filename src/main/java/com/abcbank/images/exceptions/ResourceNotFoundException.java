package com.abcbank.images.exceptions;

/** Thrown when a requested entity doesn't exist (wrong id, no matching department/queue/transition, etc.) 
 * — maps to HTTP 404. */

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}