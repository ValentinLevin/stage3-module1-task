package com.mjc.school.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.EditNewsRequestDTO;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.exception.AuthorNotFoundServiceException;
import com.mjc.school.service.exception.CustomServiceException;
import com.mjc.school.service.exception.DTOValidationServiceException;
import com.mjc.school.service.exception.NewsNotFoundServiceException;
import com.mjc.school.service.service.NewsService;
import com.mjc.school.web.constant.RESULT_CODE;
import com.mjc.school.web.dto.AddNewsResponseDTO;
import com.mjc.school.web.dto.BaseResponseDTO;
import com.mjc.school.web.exception.CustomWebRuntimeException;
import com.mjc.school.web.exception.NoDataInRequestWebException;
import com.mjc.school.web.mapper.ResultCodeMapper;
import com.mjc.school.web.servlet.NewsItemServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EditNewsTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private NewsService newsService;
    private final ObjectMapper mapper = new JsonMapper().findAndRegisterModules();
    private ByteArrayOutputStream responseBodyStream;

    @BeforeEach
    void setup() throws IOException {
        newsService = Mockito.mock(NewsService.class);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        Mockito.when(request.getMethod()).thenReturn("PUT");

        responseBodyStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(responseBodyStream);
        Mockito.when(response.getWriter()).thenReturn(writer);
    }

    @Test
    @DisplayName("Editing news. Successful, no errors")
    void editNewsSuccessTest() throws IOException, ServletException, CustomServiceException {
        long idForUpdate = 123L;

        String requestBody =
                "{ " +
                        "\"title\" : \"News title\", " +
                        "\"content\": \"News content\", " +
                        "\"authorId\": 12" +
                        "}";

        StringReader stringReader = new StringReader(requestBody);
        BufferedReader reader = new BufferedReader(stringReader);

        Mockito.when(request.getReader()).thenReturn(reader);

        EditNewsRequestDTO expectedRequestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                12L
        );

        NewsDTO newsDTO = new NewsDTO(
                idForUpdate,
                expectedRequestDTO.getTitle(),
                expectedRequestDTO.getContent(),
                new AuthorDTO(
                        expectedRequestDTO.getAuthorId(),
                        ""
                )
        );

        Mockito.when(request.getPathInfo()).thenReturn(String.valueOf(idForUpdate));
        Mockito.when(newsService.update(Mockito.any(), Mockito.any())).thenReturn(newsDTO);

        new NewsItemServlet(newsService).service(request, response);

        ArgumentCaptor<EditNewsRequestDTO> editNewsRequestDTOArgumentCaptor = ArgumentCaptor.forClass(EditNewsRequestDTO.class);
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(newsService).update(idArgumentCaptor.capture(), editNewsRequestDTOArgumentCaptor.capture());

        EditNewsRequestDTO actualRequestDTO = editNewsRequestDTOArgumentCaptor.getValue();
        long actualIdForUpdate = idArgumentCaptor.getValue();

        assertThat(actualRequestDTO).isEqualTo(expectedRequestDTO);
        assertThat(actualIdForUpdate).isEqualTo(idForUpdate);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);

        assertThat(responseBodyStream.size()).isNotZero();
        AddNewsResponseDTO responseBody = mapper.readValue(responseBodyStream.toByteArray(), AddNewsResponseDTO.class);

        assertThat(responseBody.getErrorCode()).isZero();
        assertThat(responseBody.getErrorMessage()).isNullOrEmpty();
        assertThat(responseBody.getData())
                .isNotNull()
                .extracting("title", "content", "author.id")
                .containsExactly(
                        expectedRequestDTO.getTitle(),
                        expectedRequestDTO.getContent(),
                        expectedRequestDTO.getAuthorId()
                );
    }

    @Test()
    @DisplayName("News editing. News not found.")
    void edit_notFound() throws IOException, CustomServiceException, ServletException {
        long idForUpdate = 123L;

        String requestBody =
                "{ " +
                        "\"title\" : \"News title\", " +
                        "\"content\": \"News content\", " +
                        "\"authorId\": 12" +
                        "}";

        StringReader stringReader = new StringReader(requestBody);
        BufferedReader reader = new BufferedReader(stringReader);

        Mockito.when(request.getReader()).thenReturn(reader);

        Mockito.when(request.getPathInfo()).thenReturn(String.valueOf(idForUpdate));
        NewsNotFoundServiceException expectedServiceException = new NewsNotFoundServiceException(idForUpdate);
        Mockito.when(newsService.update(Mockito.any(), Mockito.any())).thenThrow(expectedServiceException);

        RESULT_CODE expectedResultCode = ResultCodeMapper.getResultCode(NewsNotFoundServiceException.class);

        new NewsItemServlet(newsService).service(request, response);

        assertThat(expectedResultCode).isNotNull();
        Mockito.verify(response).setStatus(expectedResultCode.getHttpStatus());
        assertThat(responseBodyStream.size()).isNotZero();

        BaseResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), BaseResponseDTO.class);

        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedResultCode.getErrorCode());
        assertThat(actualResponseBody.getErrorMessage()).isEqualTo(expectedServiceException.getMessage());
    }

    @Test
    @DisplayName("News editing. Author not found")
    void newsEditing_AuthorNotFound() throws IOException, ServletException, CustomServiceException {
        long idForUpdate = 123L;

        String requestBody =
                "{ " +
                        "\"title\" : \"News title\", " +
                        "\"content\": \"News content\", " +
                        "\"authorId\": 12" +
                        "}";

        StringReader stringReader = new StringReader(requestBody);
        BufferedReader reader = new BufferedReader(stringReader);

        Mockito.when(request.getReader()).thenReturn(reader);
        Mockito.when(request.getPathInfo()).thenReturn(String.valueOf(idForUpdate));
        AuthorNotFoundServiceException authorNotFoundServiceException = new AuthorNotFoundServiceException(12);
        Mockito.when(newsService.update(Mockito.any(), Mockito.any())).thenThrow(authorNotFoundServiceException);

        RESULT_CODE expectedResultCode = ResultCodeMapper.getResultCode(AuthorNotFoundServiceException.class);

        new NewsItemServlet(newsService).service(request, response);

        Mockito.verify(response).setStatus(expectedResultCode.getHttpStatus());
        assertThat(responseBodyStream.size()).isNotZero();

        BaseResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), BaseResponseDTO.class);

        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedResultCode.getErrorCode());
        assertThat(actualResponseBody.getErrorMessage()).isEqualTo(authorNotFoundServiceException.getMessage());
    }

    @Test
    @DisplayName("News editing. DTO is incorrect")
    void newsEditing_DTO_is_incorrect() throws IOException, ServletException, CustomServiceException {
        long idForUpdate = 123L;

        StringReader stringReader = new StringReader("{}");
        BufferedReader reader = new BufferedReader(stringReader);

        Mockito.when(request.getReader()).thenReturn(reader);
        Mockito.when(request.getPathInfo()).thenReturn(String.valueOf(idForUpdate));
        DTOValidationServiceException validationException = new DTOValidationServiceException("Validation exceptions");
        Mockito.when(newsService.update(Mockito.any(), Mockito.any())).thenThrow(validationException);

        RESULT_CODE expectedResultCode = ResultCodeMapper.getResultCode(DTOValidationServiceException.class);

        new NewsItemServlet(newsService).service(request, response);

        assertThat(expectedResultCode).isNotNull();
        Mockito.verify(response).setStatus(expectedResultCode.getHttpStatus());
        assertThat(responseBodyStream.size()).isNotZero();

        BaseResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), BaseResponseDTO.class);

        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedResultCode.getErrorCode());
        assertThat(actualResponseBody.getErrorMessage()).isEqualTo(validationException.getMessage());
    }

    @Test
    @DisplayName("News editing. Empty request body")
    void newsEditing_emptyRequestBody() throws IOException, ServletException {
        long idForUpdate = 123L;

        StringReader stringReader = new StringReader("");
        BufferedReader reader = new BufferedReader(stringReader);

        Mockito.when(request.getReader()).thenReturn(reader);
        Mockito.when(request.getPathInfo()).thenReturn(String.valueOf(idForUpdate));

        NoDataInRequestWebException expectedException = new NoDataInRequestWebException();

        new NewsItemServlet(newsService).service(request, response);

        Mockito.verify(response).setStatus(expectedException.getHttpStatus());
        assertThat(responseBodyStream.size()).isNotZero();

        BaseResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), BaseResponseDTO.class);

        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedException.getResultCode().getErrorCode());
        assertThat(actualResponseBody.getErrorMessage()).isEqualTo(expectedException.getMessage());
    }

    @Test
    @DisplayName("News editing. Runtime exception from service layer")
    void newsEditing_serviceRuntimeException() throws IOException, ServletException, CustomServiceException {
        long idForUpdate = 123L;

        StringReader stringReader = new StringReader("{}");
        BufferedReader reader = new BufferedReader(stringReader);

        Mockito.when(request.getReader()).thenReturn(reader);
        Mockito.when(request.getPathInfo()).thenReturn(String.valueOf(idForUpdate));
        Mockito.when(newsService.update(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("runtime"));

        CustomWebRuntimeException expectedException = new CustomWebRuntimeException();

        new NewsItemServlet(newsService).service(request, response);

        Mockito.verify(response).setStatus(expectedException.getHttpStatus());
        assertThat(responseBodyStream.size()).isNotZero();

        BaseResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), BaseResponseDTO.class);

        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedException.getResultCode().getErrorCode());
    }
}
