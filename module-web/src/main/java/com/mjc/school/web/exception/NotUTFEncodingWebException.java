package com.mjc.school.web.exception;

import com.mjc.school.web.constant.RESULT_CODE;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class NotUTFEncodingWebException extends CustomWebException {
    private static final int HTTP_STATUS = SC_BAD_REQUEST;
    private static final String MESSAGE_TEMPLATE = "The message must be in UTF-8 encoding";

    public NotUTFEncodingWebException() {
        super(
                RESULT_CODE.REQUEST_IS_NOT_IN_UTF_ENCODING,
                MESSAGE_TEMPLATE,
                HTTP_STATUS
        );
    }
}
