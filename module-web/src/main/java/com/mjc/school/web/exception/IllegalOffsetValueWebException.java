package com.mjc.school.web.exception;

import com.mjc.school.web.constant.RESULT_CODE;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class IllegalOffsetValueWebException extends CustomWebException {
    private static final int HTTP_STATUS = SC_BAD_REQUEST;
    private static final String INVALID_FORMAT_MESSAGE_TEMPLATE = "The offset value is in invalid %s";
    private static final String INVALID_VALUE_MESSAGE_TEMPLATE =
            "Received value of offset is %d. The offset value must be greater than or equals to zero";

    public IllegalOffsetValueWebException(String offsetValue) {
        super(
                RESULT_CODE.ILLEGAL_OFFSET_VALUE,
                String.format(INVALID_FORMAT_MESSAGE_TEMPLATE, offsetValue),
                HTTP_STATUS
        );
    }

    public IllegalOffsetValueWebException(long offsetValue) {
        super(
                RESULT_CODE.ILLEGAL_OFFSET_VALUE,
                String.format(INVALID_VALUE_MESSAGE_TEMPLATE, offsetValue),
                HTTP_STATUS
        );
    }
}
