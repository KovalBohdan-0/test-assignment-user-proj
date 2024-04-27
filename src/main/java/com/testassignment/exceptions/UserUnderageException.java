package com.testassignment.exceptions;

public class UserUnderageException extends RuntimeException {
    public UserUnderageException(String message) {
        super(message);
    }
}
