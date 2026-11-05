package com.springblog.exception;

/** Mapped to HTTP 401 when a write path is reached without a session. */
public class UnauthenticatedException extends BlogException {

    public UnauthenticatedException() {
        super("Please sign in first.");
    }
}
