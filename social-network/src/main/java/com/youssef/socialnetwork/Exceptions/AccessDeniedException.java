package com.youssef.socialnetwork.Exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("You do not have permission to access this resource");
    }
}
