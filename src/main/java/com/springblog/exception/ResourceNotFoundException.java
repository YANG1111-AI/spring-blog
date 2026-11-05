package com.springblog.exception;

/** Mapped to HTTP 404 by the global exception handler. */
public class ResourceNotFoundException extends BlogException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
