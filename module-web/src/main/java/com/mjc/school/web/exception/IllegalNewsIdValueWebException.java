package com.mjc.school.web.exception;

import com.mjc.school.web.constant.RESULT_CODE;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class IllegalNewsIdValueWebException extends CustomWebException {
    private static final int HTTP_STATUS = SC_BAD_REQUEST;
    private static final String MESSAGE_TEMPLATE = "The news id value is invalid %s";

    public IllegalNewsIdValueWebException(String idValue) {
        super(
                RESULT_CODE.ILLEGAL_ID_VALUE,
                String.format(MESSAGE_TEMPLATE, idValue),
                HTTP_STATUS
        );
    }
}
