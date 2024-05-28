package com.mjc.school.exception;

public class AuthorNotFoundServiceException extends CustomServiceException {
    private static final String MESSAGE_TEMPLATE = "Author with id %d not found";

    public AuthorNotFoundServiceException(long id) {
        super(String.format(MESSAGE_TEMPLATE, id));
    }
}
