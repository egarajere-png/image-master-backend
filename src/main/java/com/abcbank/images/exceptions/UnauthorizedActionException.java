package com.abcbank.images.exceptions;

/** Thrown when a user is authenticated but not allowed to do the specific thing they're attempting 
 * (e.g. not a member of the queue they're trying to act on, or has no department at all) — maps to HTTP 403. */

public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }

    public UnauthorizedActionException(String message, Throwable cause) {
        super(message, cause);
    }
}