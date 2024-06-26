package com.mjc.school.servlet;

import com.mjc.school.constant.RESULT_CODE;
import com.mjc.school.dto.*;
import com.mjc.school.exception.*;
import com.mjc.school.mapper.ResultCodeMapper;
import com.mjc.school.service.NewsService;
import com.mjc.school.util.HttpServletRequestUtils;
import com.mjc.school.util.HttpServletResponseUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@WebServlet("/news/*")
@Slf4j
public class NewsItemServlet extends HttpServlet {
    private final transient NewsService newsService;

    public NewsItemServlet(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        long newsId = 0;
        BaseResponseDTO responseBody;
        RESULT_CODE resultCode;
        try {
            newsId = HttpServletRequestUtils.getIdFromPath(req);
            EditNewsRequestDTO newsDTO = HttpServletRequestUtils.readObjectFromRequestBody(req, EditNewsRequestDTO.class);
            newsDTO.setId(newsId);
            NewsDTO editedNewsDTO = newsService.update(newsDTO);
            responseBody = new UpdateNewsResponseDTO(editedNewsDTO);
            resultCode = RESULT_CODE.SUCCESS;
        } catch (IllegalNewsIdValueWebException | NotUTFEncodingWebException | NoDataInRequestWebException |
                 IllegalDataFormatWebException | DTOValidationServiceException | NullNewsIdServiceException |
                 NewsNotFoundServiceException | NullAuthorIdServiceException | AuthorNotFoundServiceException e)
        {
            resultCode = ResultCodeMapper.getResultCode(e.getClass());
            responseBody = new BaseResponseDTO(resultCode, e);
        } catch (CustomWebRuntimeException e) {
            resultCode = e.getResultCode();
            responseBody = new BaseResponseDTO(e);
        } catch (RuntimeException e) {
            log.error("Error when processing a request to change news data with id {}", newsId, e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody = new BaseResponseDTO(resultCode.getErrorCode(), resultCode.getDefaultMessage());
        }

        HttpServletResponseUtils.writePayloadIntoResponseBody(resp, responseBody, resultCode.getHttpStatus());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        long newsId = 0;
        BaseResponseDTO responseBody;
        RESULT_CODE resultCode;
        try {
            newsId = HttpServletRequestUtils.getIdFromPath(req);
            NewsDTO newsDTO = newsService.readById(newsId);
            responseBody = new GetNewsItemResponseDTO(newsDTO);
            resultCode = RESULT_CODE.SUCCESS;
        } catch (NullAuthorIdServiceException | AuthorNotFoundServiceException e) {
            log.error("Unexpected error when requesting news by id {}", newsId, e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody =
                    new BaseResponseDTO(
                            resultCode.getErrorCode(),
                            "An unexpected error occurred while processing the request"
                    );
        } catch (NullNewsIdServiceException | NewsNotFoundServiceException | IllegalNewsIdValueWebException |
                 CustomWebRuntimeException e) {
            resultCode = ResultCodeMapper.getResultCode(e.getClass());
            responseBody = new BaseResponseDTO(resultCode, e);
        } catch (RuntimeException e) {
            log.error("Error when requesting news by id {}", newsId, e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody = new BaseResponseDTO(resultCode.getErrorCode(), resultCode.getDefaultMessage());
        }

        HttpServletResponseUtils.writePayloadIntoResponseBody(resp, responseBody, resultCode.getHttpStatus());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        long newsId = 0;
        BaseResponseDTO responseBody;
        RESULT_CODE resultCode;
        try {
            newsId = HttpServletRequestUtils.getIdFromPath(req);
            newsService.deleteById(newsId);
            resultCode = RESULT_CODE.SUCCESS;
            responseBody = new BaseResponseDTO(resultCode.getErrorCode());
        } catch (IllegalNewsIdValueWebException | NullNewsIdServiceException | NewsNotFoundServiceException e) {
            resultCode = ResultCodeMapper.getResultCode(e.getClass());
            responseBody = new BaseResponseDTO(resultCode, e);
        } catch (RuntimeException e) {
            log.error("Error when deleting news by id {}", newsId, e);
            resultCode = RESULT_CODE.UNEXPECTED_ERROR;
            responseBody = new BaseResponseDTO(resultCode.getErrorCode(), resultCode.getDefaultMessage());
        }
        HttpServletResponseUtils.writePayloadIntoResponseBody(resp, responseBody, resultCode.getHttpStatus());
    }
}
