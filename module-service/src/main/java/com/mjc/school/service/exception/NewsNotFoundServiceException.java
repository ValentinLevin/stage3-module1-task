package com.mjc.school.service.exception;

public class NewsNotFoundServiceException extends CustomServiceException {
    private static final String MESSAGE_TEMPLATE = "Not found news with id %d";

    public NewsNotFoundServiceException(long id) {
        super(String.format(MESSAGE_TEMPLATE, id));
    }
}
