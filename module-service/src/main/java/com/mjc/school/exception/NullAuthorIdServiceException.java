package com.mjc.school.exception;

public class NullAuthorIdServiceException extends CustomServiceException {
    private static final String MESSAGE_TEMPLATE = "Incorrect author id value passed: null";

    public NullAuthorIdServiceException() {
        super(MESSAGE_TEMPLATE);
    }
}
