package com.mjc.school.web.exception;

import com.mjc.school.web.constant.RESULT_CODE;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class IllegalDataFormatWebException extends CustomWebException {
    private static final int HTTP_STATUS = SC_BAD_REQUEST;
    private static final String MESSAGE_TEMPLATE_WITH_DATA = "Error in request data format, received data %s";

    public IllegalDataFormatWebException(String data) {
        super(
                RESULT_CODE.ILLEGAL_REQUEST_DATA_FORMAT,
                String.format(MESSAGE_TEMPLATE_WITH_DATA, data),
                HTTP_STATUS
        );
    }
}
