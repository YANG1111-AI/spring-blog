package com.springblog.exception;

/** Mapped to HTTP 409: the request collides with an existing username or email. */
public class ConflictException extends BlogException {

    public ConflictException(String message) {
        super(message);
    }
}
