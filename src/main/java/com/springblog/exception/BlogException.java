package com.springblog.exception;

/**
 * Base class for expected failures that should surface as a readable message
 * rather than a stack trace.
 */
public class BlogException extends RuntimeException {

    public BlogException(String message) {
        super(message);
    }

    public BlogException(String message, Throwable cause) {
        super(message, cause);
    }
}
