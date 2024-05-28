package com.mjc.school.exception;

import com.mjc.school.constant.RESULT_CODE;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class CustomWebRuntimeException extends RuntimeException {
    private static final int HTTP_STATUS = SC_INTERNAL_SERVER_ERROR;
    private static final String MESSAGE_TEMPLATE = "An unexpected error occurred while processing the request";

    public CustomWebRuntimeException() {
        super(MESSAGE_TEMPLATE);
    }

    public int getHttpStatus() {
        return HTTP_STATUS;
    }

    public RESULT_CODE getResultCode() {
        return RESULT_CODE.UNEXPECTED_ERROR;
    }
}
