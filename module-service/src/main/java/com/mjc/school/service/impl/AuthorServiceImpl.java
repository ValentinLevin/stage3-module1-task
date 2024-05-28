package com.mjc.school.service.impl;

import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.exception.AuthorNotFoundServiceException;
import com.mjc.school.exception.EntityNotFoundException;
import com.mjc.school.exception.KeyNullReferenceException;
import com.mjc.school.exception.NullAuthorIdServiceException;
import com.mjc.school.mapper.AuthorMapper;
import com.mjc.school.model.AuthorModel;
import com.mjc.school.repository.Repository;
import com.mjc.school.service.AuthorService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class AuthorServiceImpl implements AuthorService {
    private final Repository<AuthorModel> authorRepository;
    private static final Validator validator;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    public AuthorServiceImpl(Repository<AuthorModel> authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public boolean existsById(Long id) throws AuthorNotFoundServiceException {
        try {
            return this.authorRepository.existsById(id);
        } catch (KeyNullReferenceException e) {
            throw new AuthorNotFoundServiceException(id);
        }
    }

    @Override
    public AuthorDTO readById(Long id) throws NullAuthorIdServiceException, AuthorNotFoundServiceException {
        AuthorModel authorModel;
        try {
            authorModel = this.authorRepository.readById(id);
        } catch (KeyNullReferenceException e) {
            throw new NullAuthorIdServiceException();
        } catch (EntityNotFoundException e) {
            throw new AuthorNotFoundServiceException(id);
        }

        return AuthorMapper.toAuthorDTO(authorModel);
    }

    @Override
    public Map<Long, AuthorDTO> readMap()  {
        return this.authorRepository.readAll().stream()
                        .map(AuthorMapper::toAuthorDTO)
                        .collect(Collectors.toMap(AuthorDTO::getId, item -> item));
    }
}
