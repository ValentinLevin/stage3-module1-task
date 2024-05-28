package com.mjc.school.service.impl;

import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.dto.EditNewsRequestDTO;
import com.mjc.school.dto.NewsDTO;
import com.mjc.school.exception.*;
import com.mjc.school.mapper.NewsMapper;
import com.mjc.school.model.NewsModel;
import com.mjc.school.repository.Repository;
import com.mjc.school.service.AuthorService;
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
    private final Repository<NewsModel> newsRepository;
    private final AuthorService authorService;
    private static final Validator validator;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    public NewsServiceImpl(
            Repository<NewsModel> newsRepository,
            AuthorService authorService
    ) {
        this.newsRepository = newsRepository;
        this.authorService = authorService;
    }

    @Override
    public NewsDTO create(
            EditNewsRequestDTO newsDTO
    ) throws DTOValidationServiceException, AuthorNotFoundServiceException, NullAuthorIdServiceException, NewsNotFoundServiceException, NullNewsIdServiceException {
        validateDTO(newsDTO);

        if (!authorService.existsById(newsDTO.getAuthorId())) {
            throw new AuthorNotFoundServiceException(newsDTO.getAuthorId());
        }

        NewsModel newsModel = NewsMapper.fromEditNewsRequestDTO(newsDTO);
        newsModel.setCreateDate(LocalDateTime.now());
        newsModel.setLastUpdateDate(newsModel.getCreateDate());

        try {
            newsModel = newsRepository.create(newsModel);
        } catch (EntityValidationException | EntityNullReferenceException e) {
            throw new DTOValidationServiceException(e.getMessage());
        }

        return readById(newsModel.getId());
    }

    @Override
    public NewsDTO update(
            EditNewsRequestDTO newsDTO
    ) throws DTOValidationServiceException, NullNewsIdServiceException, NewsNotFoundServiceException, AuthorNotFoundServiceException, NullAuthorIdServiceException {
        if (newsDTO.getId() == null || newsDTO.getId() <= 0) {
            throw new DTOValidationServiceException(String.format("Incorrect news id value %d", newsDTO.getId()));
        }

        validateDTO(newsDTO);

        NewsModel newsModel;
        try {
            newsModel = newsRepository.readById(newsDTO.getId());
        } catch (KeyNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityNotFoundException e) {
            throw new NewsNotFoundServiceException(newsDTO.getId());
        }

        if (!authorService.existsById(newsDTO.getAuthorId())) {
            throw new AuthorNotFoundServiceException(newsDTO.getAuthorId());
        }

        newsModel.setTitle(newsDTO.getTitle());
        newsModel.setContent(newsDTO.getContent());
        newsModel.setAuthorId(newsDTO.getAuthorId());
        newsModel.setLastUpdateDate(LocalDateTime.now());

        try {
            newsModel = newsRepository.update(newsModel);
        } catch (EntityNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityValidationException e) {
            throw new DTOValidationServiceException(e.getMessage());
        }

        return readById(newsModel.getId());
    }

    @Override
    public NewsDTO readById(Long id) throws NullNewsIdServiceException, NewsNotFoundServiceException, NullAuthorIdServiceException, AuthorNotFoundServiceException {
        NewsModel newsModel;
        try {
            newsModel = this.newsRepository.readById(id);
        } catch (KeyNullReferenceException e) {
            throw new NullNewsIdServiceException();
        } catch (EntityNotFoundException e) {
            throw new NewsNotFoundServiceException(id);
        }

        AuthorDTO authorDTO = this.authorService.readById(newsModel.getAuthorId());

        NewsDTO newsDTO = NewsMapper.toNewsDTO(newsModel);
        newsDTO.setAuthor(authorDTO);

        return newsDTO;
    }

    @Override
    public List<NewsDTO> readAll() throws AuthorNotFoundServiceException {
        return this.readPage(0, -1);
    }

    @Override
    public List<NewsDTO> readPage(long offset, long limit) throws AuthorNotFoundServiceException {
        Map<Long, AuthorDTO> authors = this.authorService.readMap();

        List<NewsModel> newsModels;
        if (offset == 0 && limit == -1) {
            newsModels = this.newsRepository.readAll();
        } else {
            newsModels = this.newsRepository.readPage(offset, limit);
        }

        List<NewsDTO> newsDTOList = new ArrayList<>();
        for (NewsModel newsModelItem : newsModels) {
            AuthorDTO authorDTO = authors.get(newsModelItem.getAuthorId());
            if (authorDTO == null) {
                throw new AuthorNotFoundServiceException(newsModelItem.getAuthorId());
            }
            NewsDTO newsDTO = NewsMapper.toNewsDTO(newsModelItem);
            newsDTO.setAuthor(authorDTO);
            newsDTOList.add(newsDTO);
        }
        return newsDTOList;
    }

    @Override
    public Boolean deleteById(Long id) throws NullNewsIdServiceException, NewsNotFoundServiceException {
        try {
            return this.newsRepository.delete(id);
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
