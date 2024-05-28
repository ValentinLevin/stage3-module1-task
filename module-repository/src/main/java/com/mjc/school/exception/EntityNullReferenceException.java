package com.mjc.school.exception;

public class EntityNullReferenceException extends CustomRepositoryException {
    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Null was passed as an object to be saved";

    public EntityNullReferenceException() {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE));
    }
}
