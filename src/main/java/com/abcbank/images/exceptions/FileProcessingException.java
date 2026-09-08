package com.abcbank.images.exceptions;

/** Thrown for genuine file I/O failures during upload/storage 
 * — maps to HTTP 500. Business-logic failures during upload (bad transition config, etc.) are deliberately NOT wrapped in this anymore, see UploadService. */

public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
