package com.mjc.school.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mjc.school.web.constant.RESULT_CODE;
import com.mjc.school.web.exception.CustomWebRuntimeException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"errorCode", "errorMessage"})
public class BaseResponseDTO {
    @JsonProperty("errorCode")
    private final int errorCode;

    @JsonProperty("errorMessage")
    private final String errorMessage;

    public BaseResponseDTO(
            int errorCode,
            String errorMessage
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseResponseDTO() {
        this(
                RESULT_CODE.SUCCESS.getErrorCode(),
                null
        );
    }

    public BaseResponseDTO(int errorCode) {
        this(
                errorCode,
                null
        );
    }

    public BaseResponseDTO(CustomWebRuntimeException exception) {
        this(
                exception.getResultCode().getErrorCode(),
                exception.getMessage()
        );
    }

    public BaseResponseDTO(RESULT_CODE resultCode, Throwable throwable) {
        this(resultCode.getErrorCode(), throwable.getMessage());
    }
}
