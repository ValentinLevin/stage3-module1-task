package com.mjc.school.service.exception;

public class NullNewsIdServiceException extends CustomServiceException {
    private static final String MESSAGE_TEMPLATE = "Incorrect news id value passed: null";

    public NullNewsIdServiceException() {
        super(MESSAGE_TEMPLATE);
    }
}
