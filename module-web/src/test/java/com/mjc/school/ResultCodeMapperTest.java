package com.mjc.school;

import com.mjc.school.exception.*;
import com.mjc.school.mapper.ResultCodeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ResultCodeMapperTest {
    static Stream<Class<? extends Exception>> exceptionSource() {
        return Stream.of(
                CustomWebRuntimeException.class,
                IllegalDataFormatWebException.class,
                IllegalLimitValueWebException.class,
                IllegalNewsIdValueWebException.class,
                IllegalOffsetValueWebException.class,
                NoDataInRequestWebException.class,
                NotUTFEncodingWebException.class,
                AuthorNotFoundServiceException.class,
                CustomServiceException.class,
                DTOValidationServiceException.class,
                NewsNotFoundServiceException.class,
                NullAuthorIdServiceException.class,
                NullNewsIdServiceException.class
        );
    }

    @ParameterizedTest
    @MethodSource("exceptionSource")
    @DisplayName("Checking the presence of mapping for all possible exceptions")
    void test(Class<? extends Exception> exceptionClass) {
        assertThat(ResultCodeMapper.getResultCode(exceptionClass)).isNotNull();
    }
}
