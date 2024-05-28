package com.mjc.school.web.mapper;

import com.mjc.school.service.exception.*;
import com.mjc.school.web.constant.RESULT_CODE;
import com.mjc.school.web.exception.*;

import java.util.HashMap;
import java.util.Map;

public class ResultCodeMapper {
    private static final Map<Class<?>, RESULT_CODE> errorCodes = new HashMap<>();

    static {
        errorCodes.put(AuthorNotFoundServiceException.class, RESULT_CODE.AUTHOR_NOT_FOUND);
        errorCodes.put(NewsNotFoundServiceException.class, RESULT_CODE.NEWS_NOT_FOUND);
        errorCodes.put(DTOValidationServiceException.class, RESULT_CODE.DATA_VALIDATION);
        errorCodes.put(NullAuthorIdServiceException.class, RESULT_CODE.DATA_VALIDATION);
        errorCodes.put(NullNewsIdServiceException.class, RESULT_CODE.ILLEGAL_ID_VALUE);
        errorCodes.put(CustomServiceException.class, RESULT_CODE.UNEXPECTED_ERROR);
        errorCodes.put(CustomWebRuntimeException.class, RESULT_CODE.UNEXPECTED_ERROR);
        errorCodes.put(IllegalLimitValueWebException.class, RESULT_CODE.ILLEGAL_LIMIT_VALUE);
        errorCodes.put(IllegalOffsetValueWebException.class, RESULT_CODE.ILLEGAL_OFFSET_VALUE);
        errorCodes.put(IllegalNewsIdValueWebException.class, RESULT_CODE.ILLEGAL_ID_VALUE);
        errorCodes.put(IllegalDataFormatWebException.class, RESULT_CODE.ILLEGAL_DATA_FORMAT);
        errorCodes.put(NoDataInRequestWebException.class, RESULT_CODE.NO_DATA_IN_REQUEST);
        errorCodes.put(NotUTFEncodingWebException.class, RESULT_CODE.NOT_UTF_ENCODING);
    }

    private ResultCodeMapper() {}

    public static RESULT_CODE getResultCode(Class<?> clazz) {
        return errorCodes.get(clazz);
    }
}
