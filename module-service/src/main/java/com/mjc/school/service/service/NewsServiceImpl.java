package com.mjc.school.service.service;

import com.mjc.school.repository.exception.EntityNotFoundException;
import com.mjc.school.repository.exception.EntityNullReferenceException;
import com.mjc.school.repository.exception.EntityValidationException;
import com.mjc.school.repository.exception.KeyNullReferenceException;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.repository.Repository;
import com.mjc.school.service.dto.EditNewsRequestDTO;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.exception.*;
import com.mjc.school.service.mapper.AuthorMapper;
import com.mjc.school.service.mapper.NewsMapper;
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
    public NewsDTO add(
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
            news = newsRepository.save(news);
        } catch (EntityValidationException | EntityNullReferenceException e) {
            throw new DTOValidationServiceException(e.getMessage());
        }

        return findById(news.getId());
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
            news = newsRepository.findById(newsId);
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
            news = newsRepository.save(news);
        } catch (EntityNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityValidationException e) {
            throw new DTOValidationServiceException(e.getMessage());
        }

        return findById(news.getId());
    }

    @Override
    public NewsDTO findById(long id) throws NullNewsIdServiceException, NewsNotFoundServiceException, NullAuthorIdServiceException, AuthorNotFoundServiceException {
        News news;
        try {
            news = this.newsRepository.findById(id);
        } catch (KeyNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityNotFoundException e) {
            throw new NewsNotFoundServiceException(id);
        }

        Author author;
        try {
            author = this.authorRepository.findById(news.getAuthorId());
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
    public List<NewsDTO> findAll() throws AuthorNotFoundServiceException {
        return this.findAll(0, -1);
    }

    @Override
    public List<NewsDTO> findAll(long offset, long limit) throws AuthorNotFoundServiceException {
        Map<Long, Author> authors =
                this.authorRepository.findAll().stream()
                        .collect(Collectors.toMap(Author::getId, item -> item));

        List<News> news;
        if (offset == 0 && limit == -1) {
            news = this.newsRepository.findAll();
        } else {
            news = this.newsRepository.findAll(offset, limit);
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
