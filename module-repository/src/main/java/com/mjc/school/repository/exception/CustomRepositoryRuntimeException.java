package com.mjc.school.repository.exception;

import java.io.Serial;

public class CustomRepositoryRuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5844686610864219067L;

    public CustomRepositoryRuntimeException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public CustomRepositoryRuntimeException(String errorMessage) {
        super(errorMessage);
    }
}
