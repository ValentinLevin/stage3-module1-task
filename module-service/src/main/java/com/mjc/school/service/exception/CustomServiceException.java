package com.mjc.school.service.exception;

import java.io.Serial;

public class CustomServiceException extends Exception {
    @Serial
    private static final long serialVersionUID = 8208852846725275315L;

    public CustomServiceException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    public CustomServiceException(String errorMessage) {
        super(errorMessage);
    }
}
