package com.mjc.school.repository.exception;

public class KeyNullReferenceException extends CustomRepositoryException {
    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Null was passed as an object to be saved";

    public KeyNullReferenceException() {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE));
    }
}
