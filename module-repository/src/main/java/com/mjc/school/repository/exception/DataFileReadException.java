package com.mjc.school.repository.exception;

public class DataFileReadException extends CustomRepositoryRuntimeException {
    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Error on read data file with name %s";

    public DataFileReadException(String filename, Throwable e) {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE, filename), e);
    }
}
