package com.mjc.school.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.EditNewsRequestDTO;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.exception.AuthorNotFoundServiceException;
import com.mjc.school.service.exception.CustomServiceException;
import com.mjc.school.service.exception.DTOValidationServiceException;
import com.mjc.school.service.service.NewsService;
import com.mjc.school.web.constant.RESULT_CODE;
import com.mjc.school.web.dto.AddNewsResponseDTO;
import com.mjc.school.web.exception.NoDataInRequestWebException;
import com.mjc.school.web.mapper.ResultCodeMapper;
import com.mjc.school.web.servlet.NewsServlet;
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

class AddNewNewsTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private NewsService newsService;

    private ByteArrayOutputStream responseBodyStream;
    private final ObjectMapper mapper = new JsonMapper().findAndRegisterModules();

    @BeforeEach
    void setup() throws IOException {
        newsService = Mockito.mock(NewsService.class);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        Mockito.when(request.getMethod()).thenReturn("POST");

        responseBodyStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(responseBodyStream);
        Mockito.when(response.getWriter()).thenReturn(writer);
    }

    @Test
    @DisplayName("Adding news. Successful, no errors")
    void createNewsTest() throws IOException, ServletException, CustomServiceException {
        String requestBody =
                "{ " +
                        "\"title\" : \"News title\", " +
                        "\"content\": \"News content\", " +
                        "\"authorId\": 12" +
                        "}";

        StringReader stringReader = new StringReader(requestBody);
        BufferedReader reader = new BufferedReader(stringReader);

        Mockito.when(request.getReader()).thenReturn(reader);

        EditNewsRequestDTO exceptedRequestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                12L
        );

        NewsDTO createdNews = new NewsDTO(
                123L,
                exceptedRequestDTO.getTitle(),
                exceptedRequestDTO.getContent(),
                "",
                "",
                new AuthorDTO(exceptedRequestDTO.getAuthorId(), "")
        );
        Mockito.when(newsService.add(Mockito.any(EditNewsRequestDTO.class))).thenReturn(createdNews);

        new NewsServlet(newsService).service(request, response);

        ArgumentCaptor<EditNewsRequestDTO> editNewsRequestDTOArgumentCaptor = ArgumentCaptor.forClass(EditNewsRequestDTO.class);
        Mockito.verify(newsService).add(editNewsRequestDTOArgumentCaptor.capture());

        EditNewsRequestDTO actualRequestDTO = editNewsRequestDTOArgumentCaptor.getValue();

        assertThat(actualRequestDTO).isEqualTo(exceptedRequestDTO);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(response).addHeader("Location", "/news/" + createdNews.getId());

        assertThat(responseBodyStream.size()).isNotZero();
        AddNewsResponseDTO responseBody = mapper.readValue(responseBodyStream.toByteArray(), AddNewsResponseDTO.class);

        assertThat(responseBody.getErrorCode()).isZero();
        assertThat(responseBody.getErrorMessage()).isNullOrEmpty();
        assertThat(responseBody.getData())
                .isNotNull()
                .extracting("title", "content", "author.id")
                .containsExactly(
                        exceptedRequestDTO.getTitle(),
                        exceptedRequestDTO.getContent(),
                        exceptedRequestDTO.getAuthorId()
                );
    }

    @Test
    @DisplayName("Adding news. News data was not transmitted in the request body")
    void requestWithoutBody() throws IOException, ServletException {
        StringReader reader = new StringReader("");
        BufferedReader bufferedReader = new BufferedReader(reader);
        Mockito.when(request.getReader()).thenReturn(bufferedReader);

        new NewsServlet(newsService).service(request, response);

        AddNewsResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), AddNewsResponseDTO.class);

        NoDataInRequestWebException expectedException = new NoDataInRequestWebException();

        Mockito.verify(response).setStatus(expectedException.getHttpStatus());
        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedException.getResultCode().getErrorCode());
        assertThat(actualResponseBody.getErrorMessage()).isEqualTo(expectedException.getMessage());
    }

    @Test
    @DisplayName("Adding news. Incorrect news title value. Returned error status when adding")
    void incorrectNewsData_emptyTitle() throws IOException, ServletException, CustomServiceException {
        String requestBody =
                "{ " +
                        "\"title\" : \"\", " +
                        "\"content\": \"News content\", " +
                        "\"authorId\": 12" +
                "}";

        StringReader reader = new StringReader(requestBody);
        BufferedReader bufferedReader = new BufferedReader(reader);
        Mockito.when(request.getReader()).thenReturn(bufferedReader);

        Mockito.when(newsService.add(Mockito.any())).thenThrow(new DTOValidationServiceException(requestBody));

        RESULT_CODE expectedResultCode = ResultCodeMapper.getResultCode(DTOValidationServiceException.class);

        new NewsServlet(newsService).service(request, response);

        assertThat(expectedResultCode).isNotNull();
        Mockito.verify(response).setStatus(expectedResultCode.getHttpStatus());

        AddNewsResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), AddNewsResponseDTO.class);
        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedResultCode.getErrorCode());
    }

    @Test
    @DisplayName("Adding news. Empty news object. Returned error status when adding")
    void incorrectNewsData_emptyObject() throws IOException, ServletException, CustomServiceException {
        StringReader reader = new StringReader("{}");
        BufferedReader bufferedReader = new BufferedReader(reader);
        Mockito.when(request.getReader()).thenReturn(bufferedReader);

        Mockito.when(newsService.add(Mockito.any())).thenThrow(DTOValidationServiceException.class);

        RESULT_CODE expectedResultCode = ResultCodeMapper.getResultCode(DTOValidationServiceException.class);

        new NewsServlet(newsService).service(request, response);

        assertThat(expectedResultCode).isNotNull();
        Mockito.verify(response).setStatus(expectedResultCode.getHttpStatus());

        AddNewsResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), AddNewsResponseDTO.class);
        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedResultCode.getErrorCode());
    }

    @Test
    @DisplayName("Adding news. Incorrect authorId")
    void incorrectNewsData_incorrectAuthorId() throws IOException, ServletException, CustomServiceException {
        String requestBody =
                "{ " +
                        "\"title\" : \"News title\", " +
                        "\"content\": \"News content\", " +
                        "\"authorId\": 0" +
                "}";

        StringReader reader = new StringReader(requestBody);
        BufferedReader bufferedReader = new BufferedReader(reader);
        Mockito.when(request.getReader()).thenReturn(bufferedReader);

        Mockito.when(newsService.add(Mockito.any())).thenThrow(AuthorNotFoundServiceException.class);

        RESULT_CODE expectedResultCode = ResultCodeMapper.getResultCode(AuthorNotFoundServiceException.class);

        new NewsServlet(newsService).service(request, response);

        assertThat(expectedResultCode).isNotNull();
        Mockito.verify(response).setStatus(expectedResultCode.getHttpStatus());

        AddNewsResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), AddNewsResponseDTO.class);
        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedResultCode.getErrorCode());
    }
}
