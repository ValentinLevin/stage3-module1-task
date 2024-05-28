package com.mjc.school.repository.exception;

import java.io.Serial;

public class CustomRepositoryException extends Exception {
    @Serial
    private static final long serialVersionUID = 8208852846725275315L;

    public CustomRepositoryException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public CustomRepositoryException(String errorMessage) {
        super(errorMessage);
    }
}
