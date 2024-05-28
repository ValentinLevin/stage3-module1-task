package com.mjc.school.exception;

import com.mjc.school.constant.RESULT_CODE;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class NoDataInRequestWebException extends CustomWebException {
    private static final int HTTP_STATUS = SC_BAD_REQUEST;
    private static final String MESSAGE_TEMPLATE = "No data was sent in the request";

    public NoDataInRequestWebException() {
        super(
                RESULT_CODE.NO_DATA_IN_REQUEST,
                MESSAGE_TEMPLATE,
                HTTP_STATUS
        );

    }
}
