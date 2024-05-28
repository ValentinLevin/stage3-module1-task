package com.mjc.school.repository.exception;

import java.io.Serial;

public class DataFileNotFoundException extends CustomRepositoryRuntimeException {
    @Serial
    private static final long serialVersionUID = 2373539337608624346L;

    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Not found data file with name %s";

    public DataFileNotFoundException(String filename) {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE, filename));
    }
}
