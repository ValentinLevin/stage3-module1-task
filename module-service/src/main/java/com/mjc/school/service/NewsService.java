package com.mjc.school.service;

import com.mjc.school.dto.EditNewsRequestDTO;
import com.mjc.school.dto.NewsDTO;
import com.mjc.school.exception.*;

import java.util.List;

public interface NewsService {
    NewsDTO create(EditNewsRequestDTO newsDTO) throws DTOValidationServiceException, AuthorNotFoundServiceException, NullAuthorIdServiceException, NewsNotFoundServiceException, NullNewsIdServiceException;
    NewsDTO update(EditNewsRequestDTO newsDTO) throws DTOValidationServiceException, NullNewsIdServiceException, NewsNotFoundServiceException, NullAuthorIdServiceException, AuthorNotFoundServiceException;
    NewsDTO readById(Long id) throws NullNewsIdServiceException, NewsNotFoundServiceException, NullAuthorIdServiceException, AuthorNotFoundServiceException;
    List<NewsDTO> readAll() throws AuthorNotFoundServiceException;

    /**
     * @param offset number of news to skip. If the value is zero, the news items will be taken from the first one
     * @param limit number of news no more than the method should return. If the value of "limit" parameter is -1, all the news items of the dataset will be returned
     * @return list of news
     */
    List<NewsDTO> readPage(long offset, long limit) throws AuthorNotFoundServiceException;
    Boolean deleteById(Long id) throws NullNewsIdServiceException, NewsNotFoundServiceException;
    long count();
}
