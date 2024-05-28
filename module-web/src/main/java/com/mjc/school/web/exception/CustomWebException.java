package com.mjc.school.web.exception;

import com.mjc.school.web.constant.RESULT_CODE;
import lombok.Getter;

@Getter
public class CustomWebException extends Exception {
    private final int httpStatus;
    private final RESULT_CODE resultCode;

    public CustomWebException(RESULT_CODE resultCode, String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.resultCode = resultCode;
    }
}
