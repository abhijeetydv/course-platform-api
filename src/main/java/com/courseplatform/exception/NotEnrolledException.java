package com.courseplatform.exception;

public class NotEnrolledException extends RuntimeException {
    
    public NotEnrolledException(String message) {
        super(message);
    }
    
    public NotEnrolledException() {
        super("You must be enrolled in this course to mark subtopics as complete");
    }
}
