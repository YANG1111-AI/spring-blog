package com.springblog.exception;

/** Mapped to HTTP 403: the caller is authenticated but not the owner of the target. */
public class NotOwnerException extends BlogException {

    public NotOwnerException(String message) {
        super(message);
    }
}
