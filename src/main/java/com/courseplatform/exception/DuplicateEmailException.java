package com.courseplatform.exception;

public class DuplicateEmailException extends RuntimeException {
    
    public DuplicateEmailException(String message) {
        super(message);
    }
    
    public DuplicateEmailException() {
        super("Email already exists");
    }
}
