package com.crafts.profileconsumer.exception;

public class UserProfileRepositoryException extends RuntimeException {

    public UserProfileRepositoryException() {
        super();
    }

    public UserProfileRepositoryException(String message) {
        super(message);
    }

    public UserProfileRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
