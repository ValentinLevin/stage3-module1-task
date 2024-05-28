package com.mjc.school.web.servlet;

import com.mjc.school.service.dto.EditNewsRequestDTO;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.exception.*;
import com.mjc.school.service.service.NewsService;
import com.mjc.school.web.constant.RESULT_CODE;
import com.mjc.school.web.dto.AddNewsResponseDTO;
import com.mjc.school.web.dto.BaseResponseDTO;
import com.mjc.school.web.dto.GetNewsListResponseDTO;
import com.mjc.school.web.exception.*;
import com.mjc.school.web.mapper.ResultCodeMapper;
import com.mjc.school.web.util.HttpServletRequestUtils;
import com.mjc.school.web.util.HttpServletResponseUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@WebServlet("/news")
@Slf4j
public class NewsServlet extends HttpServlet {
    private final transient NewsService newsService;

    public NewsServlet(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        BaseResponseDTO responseBody;
        RESULT_CODE resultCode;
        try {
            int limit = HttpServletRequestUtils.getLimitValueFromRequest(req);
            int offset = HttpServletRequestUtils.getOffsetValueFromRequest(req);
            List<NewsDTO> news = this.newsService.findAll(offset, limit);
            long totalCount = this.newsService.count();
            responseBody = new GetNewsListResponseDTO(news, offset + 1L, news.size(), totalCount);
            resultCode = RESULT_CODE.SUCCESS;
        } catch (AuthorNotFoundServiceException e) {
            log.error("An exception was thrown that should not have been thrown", e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody =
                    new BaseResponseDTO(
                            resultCode.getErrorCode(),
                            resultCode.getDefaultMessage()
                    );
        } catch (IllegalLimitValueWebException | IllegalOffsetValueWebException e) {
            resultCode = ResultCodeMapper.getResultCode(e.getClass());
            responseBody = new BaseResponseDTO(resultCode, e);
        } catch (RuntimeException e) {
            log.error("Error when processing a request to get a list of news", e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody =
                    new BaseResponseDTO(
                            resultCode.getErrorCode(),
                            resultCode.getDefaultMessage()
                    );
        }

        HttpServletResponseUtils.writePayloadIntoResponseBody(
                resp,
                responseBody,
                resultCode.getHttpStatus()
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        BaseResponseDTO responseBody;
        RESULT_CODE resultCode;
        try {
            EditNewsRequestDTO newsDTO = HttpServletRequestUtils.readObjectFromRequestBody(req, EditNewsRequestDTO.class);
            NewsDTO createdNewsDTO = newsService.add(newsDTO);

            URI createdNewsUrl = new URI("/news/" + createdNewsDTO.getId());
            resp.addHeader("Location", createdNewsUrl.getPath());

            resultCode = RESULT_CODE.ADD_SUCCESS;
            responseBody = new AddNewsResponseDTO(createdNewsDTO);
        } catch (URISyntaxException e) {
            log.error("Error on defining URL in location header of added news", e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody =
                    new BaseResponseDTO(
                            resultCode.getErrorCode(),
                            "An unexpected error occurred while processing the request "
                    );
        } catch (NullNewsIdServiceException | NewsNotFoundServiceException e) {
            log.error("An unexpected exception was thrown while adding", e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody =
                    new BaseResponseDTO(
                            resultCode.getErrorCode(),
                            "An unexpected error occurred while processing the request"
                    );
        } catch (IllegalDataFormatWebException | NoDataInRequestWebException | NotUTFEncodingWebException |
                 DTOValidationServiceException | NullAuthorIdServiceException | CustomWebRuntimeException |
                 AuthorNotFoundServiceException e
        ) {
            resultCode = ResultCodeMapper.getResultCode(e.getClass());
            responseBody = new BaseResponseDTO(resultCode, e);
        } catch (RuntimeException e) {
            log.error("Error processing a request to add news", e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody = new BaseResponseDTO(resultCode.getErrorCode(), resultCode.getDefaultMessage());
        }

        HttpServletResponseUtils.writePayloadIntoResponseBody(resp, responseBody, resultCode.getHttpStatus());
    }
}
