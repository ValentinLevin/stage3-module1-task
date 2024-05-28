package com.mjc.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mjc.school.constant.RESULT_CODE;
import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.dto.BaseResponseDTO;
import com.mjc.school.dto.GetNewsItemResponseDTO;
import com.mjc.school.dto.NewsDTO;
import com.mjc.school.exception.CustomServiceException;
import com.mjc.school.exception.IllegalNewsIdValueWebException;
import com.mjc.school.exception.NewsNotFoundServiceException;
import com.mjc.school.mapper.ResultCodeMapper;
import com.mjc.school.service.NewsService;
import com.mjc.school.servlet.NewsItemServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GetNewsByIdTest {
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

        Mockito.when(request.getMethod()).thenReturn("GET");

        responseBodyStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(responseBodyStream);
        Mockito.when(response.getWriter()).thenReturn(writer);
    }

    @Test()
    @DisplayName("Successful receipt of news")
    void successTest() throws ServletException, IOException, CustomServiceException {
        NewsDTO newsDTO =
                new NewsDTO(
                        12L,
                        "News title",
                        "News content",
                        new AuthorDTO(
                                1L,
                                "Author name"
                        )
                );

        Mockito.when(request.getPathInfo()).thenReturn("/12");

        Mockito.when(newsService.readById(Mockito.anyLong())).thenReturn(newsDTO);

        new NewsItemServlet(newsService).service(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);

        assertThat(responseBodyStream.size()).isNotZero();
        GetNewsItemResponseDTO actualResponse = mapper.readValue(responseBodyStream.toByteArray(), GetNewsItemResponseDTO.class);
        assertThat(actualResponse.getErrorCode()).isEqualTo(RESULT_CODE.SUCCESS.getErrorCode());
        assertThat(actualResponse.getData())
                .isNotNull()
                .isEqualTo(newsDTO);
    }

    @Test
    @DisplayName("Request news. No news found")
    void notFoundById() throws ServletException, IOException, CustomServiceException {
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(newsService.readById(Mockito.anyLong())).thenThrow(NewsNotFoundServiceException.class);

        new NewsItemServlet(newsService).service(request, response);

        RESULT_CODE expectedResultCode = ResultCodeMapper.getResultCode(NewsNotFoundServiceException.class);

        assertThat(expectedResultCode).isNotNull();
        Mockito.verify(response).setStatus(expectedResultCode.getHttpStatus());

        assertThat(responseBodyStream.size()).isNotZero();
        BaseResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), BaseResponseDTO.class);
        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedResultCode.getErrorCode());
    }

    @Test
    @DisplayName("Request news. The id format is incorrect")
    void incorrectId() throws ServletException, IOException {
        Mockito.when(request.getPathInfo()).thenReturn("/1_1");

        new NewsItemServlet(newsService).service(request, response);

        RESULT_CODE expectedResultCode = ResultCodeMapper.getResultCode(IllegalNewsIdValueWebException.class);

        assertThat(expectedResultCode).isNotNull();
        Mockito.verify(response).setStatus(expectedResultCode.getHttpStatus());

        assertThat(responseBodyStream.size()).isNotZero();
        GetNewsItemResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), GetNewsItemResponseDTO.class);
        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedResultCode.getErrorCode());
    }
}
