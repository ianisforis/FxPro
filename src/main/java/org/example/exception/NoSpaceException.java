package org.example.exception;

public class NoSpaceException extends RuntimeException {

    public NoSpaceException(String message) {
        super(message);
    }

    public NoSpaceException(String message, Throwable cause) {
        super(message, cause);
    }
}
