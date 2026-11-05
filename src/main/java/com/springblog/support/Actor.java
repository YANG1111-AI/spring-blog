package com.springblog.support;

/**
 * Who is performing a write, as seen by the service layer.
 *
 * Passing this explicitly instead of reading SecurityContextHolder inside a service keeps the
 * ownership rules unit-testable without a web context.
 */
public record Actor(String username, boolean administrator) {

    public boolean owns(String owner) {
        return administrator || (owner != null && owner.equals(username));
    }
}
