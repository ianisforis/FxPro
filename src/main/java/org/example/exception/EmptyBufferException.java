package org.example.exception;

public class EmptyBufferException extends RuntimeException {

    public EmptyBufferException(String message) {
        super(message);
    }

    public EmptyBufferException(String message, Throwable cause) {
        super(message, cause);
    }
}
