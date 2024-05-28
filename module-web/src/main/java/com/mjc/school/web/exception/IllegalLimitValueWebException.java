package com.mjc.school.web.exception;

import com.mjc.school.web.constant.RESULT_CODE;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class IllegalLimitValueWebException extends CustomWebException {
    private static final int HTTP_STATUS = SC_BAD_REQUEST;
    private static final String INVALID_FORMAT_MESSAGE_TEMPLATE = "The limit value is in invalid %s";
    private static final String INVALID_VALUE_MESSAGE_TEMPLATE =
            "Received limit value is %d. The limit value must be greater than or equal to zero";

    public IllegalLimitValueWebException(String limitValue) {
        super(
                RESULT_CODE.ILLEGAL_LIMIT_VALUE,
                String.format(INVALID_FORMAT_MESSAGE_TEMPLATE, limitValue),
                HTTP_STATUS
        );
    }

    public IllegalLimitValueWebException(Integer limitValue) {
        super(
                RESULT_CODE.ILLEGAL_LIMIT_VALUE,
                String.format(INVALID_VALUE_MESSAGE_TEMPLATE, limitValue),
                HTTP_STATUS
        );
    }
}
