package com.mjc.school.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.exception.CustomServiceException;
import com.mjc.school.service.service.NewsService;
import com.mjc.school.web.dto.GetNewsListResponseDTO;
import com.mjc.school.web.exception.IllegalLimitValueWebException;
import com.mjc.school.web.exception.IllegalOffsetValueWebException;
import com.mjc.school.web.servlet.NewsServlet;
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
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReadNewsListTest {
    private NewsService newsService;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private final ObjectMapper mapper = new JsonMapper().findAndRegisterModules();
    private ByteArrayOutputStream responseBodyStream;
    private final List<NewsDTO> news = List.of(
            new NewsDTO(
                    1L,
                    "News 1 title",
                    "News 1 content",
                    new AuthorDTO(
                            11L,
                            "Author 1 name"
                    )
            ),
            new NewsDTO(
                    2L,
                    "News 2 title",
                    "News 2 content",
                    new AuthorDTO(
                            22L,
                            "Author 2 name"
                    )
            ),
            new NewsDTO(
                    3L,
                    "News 3 title",
                    "News 3 content",
                    new AuthorDTO(
                            33L,
                            "Author 3 name"
                    )
            )
    );

    @BeforeEach
    void setUp() throws IOException, CustomServiceException {
        newsService = Mockito.mock(NewsService.class);
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        Mockito.when(request.getMethod()).thenReturn("GET");

        responseBodyStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(responseBodyStream);
        Mockito.when(response.getWriter()).thenReturn(printWriter);

        Mockito.when(newsService.findAll(Mockito.anyLong(), Mockito.anyLong())).thenReturn(news);
        Mockito.when(newsService.count()).thenReturn(10L);
    }

    @Test
    @DisplayName("Getting a complete list of news. Exceptions will not be thrown")
    void getAll() throws IOException, ServletException {
        Mockito.when(request.getParameter("limit")).thenReturn(null);
        Mockito.when(request.getParameter("offset")).thenReturn(null);

        new NewsServlet(newsService).service(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(response).setCharacterEncoding("UTF-8");
        Mockito.verify(response).setContentType("application/json");

        GetNewsListResponseDTO expectedResponseBody = new GetNewsListResponseDTO(news, 1, news.size(), 10L);
        GetNewsListResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), GetNewsListResponseDTO.class);

        assertThat(actualResponseBody).usingRecursiveComparison().isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("Getting a list of news. limit and offset are passed to the service")
    void testLimitAndOffset() throws ServletException, IOException, CustomServiceException {
        int limit = news.size();
        int offset = 1;
        Mockito.when(request.getParameter("limit")).thenReturn(String.valueOf(limit));
        Mockito.when(request.getParameter("offset")).thenReturn(String.valueOf(offset));

        Mockito.when(newsService.count()).thenReturn(10L);

        new NewsServlet(newsService).service(request, response);

        Mockito.verify(newsService).findAll(offset, limit);

        GetNewsListResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), GetNewsListResponseDTO.class);

        assertThat(actualResponseBody.getStart()).isEqualTo(offset+1);
        assertThat(actualResponseBody.getSize()).isEqualTo(limit);
    }

    @Test
    @DisplayName("Invalid limit value. Checking that HttpStatus and errorCode match the values from the IllegalLimitValueException")
    void incorrectLimitValue() throws ServletException, IOException {
        int limit = -1;
        Mockito.when(request.getParameter("limit")).thenReturn(String.valueOf(limit));

        new NewsServlet(newsService).service(request, response);

        GetNewsListResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), GetNewsListResponseDTO.class);

        IllegalLimitValueWebException expectedException = new IllegalLimitValueWebException(limit);

        Mockito.verify(response).setStatus(expectedException.getHttpStatus());

        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedException.getResultCode().getErrorCode());
        assertThat(actualResponseBody.getErrorMessage()).isEqualTo(expectedException.getMessage());
    }

    @Test
    @DisplayName("Invalid offset value. Checking that HttpStatus and errorCode match the values from the IllegalOffsetValueException")
    void incorrectOffsetValue() throws ServletException, IOException {
        int offset = -1;
        Mockito.when(request.getParameter("limit")).thenReturn("1");
        Mockito.when(request.getParameter("offset")).thenReturn(String.valueOf(offset));

        new NewsServlet(newsService).service(request, response);

        GetNewsListResponseDTO actualResponseBody = mapper.readValue(responseBodyStream.toByteArray(), GetNewsListResponseDTO.class);

        IllegalOffsetValueWebException expectedException = new IllegalOffsetValueWebException(offset);

        Mockito.verify(response).setStatus(expectedException.getHttpStatus());

        assertThat(actualResponseBody.getErrorCode()).isEqualTo(expectedException.getResultCode().getErrorCode());
        assertThat(actualResponseBody.getErrorMessage()).isEqualTo(expectedException.getMessage());
    }
}
