package com.mjc.school.service.impl;

import com.mjc.school.dto.EditNewsRequestDTO;
import com.mjc.school.dto.NewsDTO;
import com.mjc.school.exception.*;
import com.mjc.school.mapper.AuthorMapper;
import com.mjc.school.mapper.NewsMapper;
import com.mjc.school.model.Author;
import com.mjc.school.model.News;
import com.mjc.school.repository.Repository;
import com.mjc.school.service.NewsService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class NewsServiceImpl implements NewsService {
    private final Repository<News> newsRepository;
    private final Repository<Author> authorRepository;
    private static final Validator validator;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    public NewsServiceImpl(
            Repository<News> newsRepository,
            Repository<Author> authorRepository
    ) {
        this.newsRepository = newsRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public NewsDTO create(
            EditNewsRequestDTO newsDTO
    ) throws DTOValidationServiceException, AuthorNotFoundServiceException, NullAuthorIdServiceException, NewsNotFoundServiceException, NullNewsIdServiceException {
        validateDTO(newsDTO);

        try {
            if (!authorRepository.existsById(newsDTO.getAuthorId())) {
                throw new AuthorNotFoundServiceException(newsDTO.getAuthorId());
            }
        } catch (KeyNullReferenceException e) {
            throw new NullAuthorIdServiceException();
        }

        News news = NewsMapper.fromEditNewsRequestDTO(newsDTO);
        news.setCreateDate(LocalDateTime.now());
        news.setLastUpdateDate(news.getCreateDate());

        try {
            news = newsRepository.create(news);
        } catch (EntityValidationException | EntityNullReferenceException e) {
            throw new DTOValidationServiceException(e.getMessage());
        }

        return readById(news.getId());
    }

    @Override
    public NewsDTO update(
            Long newsId, EditNewsRequestDTO newsDTO
    ) throws DTOValidationServiceException, NullNewsIdServiceException, NewsNotFoundServiceException, AuthorNotFoundServiceException, NullAuthorIdServiceException {
        if (newsId == null || newsId <= 0) {
            throw new DTOValidationServiceException(String.format("Incorrect news id value %d", newsId));
        }

        validateDTO(newsDTO);

        News news;
        try {
            news = newsRepository.readById(newsId);
        } catch (KeyNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityNotFoundException e) {
            throw new NewsNotFoundServiceException(newsId);
        }

        try {
            if (!authorRepository.existsById(newsDTO.getAuthorId())) {
                throw new AuthorNotFoundServiceException(newsDTO.getAuthorId());
            }
        } catch (KeyNullReferenceException e) {
            throw new NullAuthorIdServiceException();
        }

        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setAuthorId(newsDTO.getAuthorId());
        news.setLastUpdateDate(LocalDateTime.now());

        try {
            news = newsRepository.update(news);
        } catch (EntityNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityValidationException e) {
            throw new DTOValidationServiceException(e.getMessage());
        }

        return readById(news.getId());
    }

    @Override
    public NewsDTO readById(long id) throws NullNewsIdServiceException, NewsNotFoundServiceException, NullAuthorIdServiceException, AuthorNotFoundServiceException {
        News news;
        try {
            news = this.newsRepository.readById(id);
        } catch (KeyNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityNotFoundException e) {
            throw new NewsNotFoundServiceException(id);
        }

        Author author;
        try {
            author = this.authorRepository.readById(news.getAuthorId());
        } catch (KeyNullReferenceException e) {
            throw new NullAuthorIdServiceException();
        } catch (EntityNotFoundException e) {
            throw new AuthorNotFoundServiceException(news.getAuthorId());
        }

        NewsDTO newsDTO = NewsMapper.toNewsDTO(news);
        newsDTO.setAuthor(author != null ? AuthorMapper.toAuthorDTO(author) : null);

        return newsDTO;
    }

    @Override
    public List<NewsDTO> readAll() throws AuthorNotFoundServiceException {
        return this.readAll(0, -1);
    }

    @Override
    public List<NewsDTO> readAll(long offset, long limit) throws AuthorNotFoundServiceException {
        Map<Long, Author> authors =
                this.authorRepository.readAll().stream()
                        .collect(Collectors.toMap(Author::getId, item -> item));

        List<News> news;
        if (offset == 0 && limit == -1) {
            news = this.newsRepository.readAll();
        } else {
            news = this.newsRepository.readAll(offset, limit);
        }

        List<NewsDTO> newsDTOList = new ArrayList<>();
        for (News newsItem : news) {
            Author author = authors.get(newsItem.getAuthorId());
            if (author == null) {
                throw new AuthorNotFoundServiceException(newsItem.getAuthorId());
            }
            NewsDTO newsDTO = NewsMapper.toNewsDTO(newsItem);
            newsDTO.setAuthor(AuthorMapper.toAuthorDTO(author));
            newsDTOList.add(newsDTO);
        }
        return newsDTOList;
    }

    @Override
    public boolean deleteById(long id) throws NullNewsIdServiceException, NewsNotFoundServiceException {
        try {
            return this.newsRepository.deleteById(id);
        } catch (KeyNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityNotFoundException e) {
            throw new NewsNotFoundServiceException(id);
        }
    }

    @Override
    public long count() {
        return this.newsRepository.count();
    }

    private <T> void validateDTO(T object) throws DTOValidationServiceException {
        if (object == null) {
            throw new DTOValidationServiceException("Passed a null object as the object to add");
        }

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
        if (!constraintViolations.isEmpty()) {
            throw new DTOValidationServiceException(
                    constraintViolations.stream()
                            .map(cv -> cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage())
                            .collect(Collectors.joining(", "))
            );
        }
    }
}
