package com.mjc.school.repository.impl;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.exception.EntityNotFoundException;
import com.mjc.school.exception.EntityNullReferenceException;
import com.mjc.school.exception.EntityValidationException;
import com.mjc.school.exception.KeyNullReferenceException;
import com.mjc.school.model.Entity;
import com.mjc.school.repository.Repository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RepositoryImpl<T extends Entity> implements Repository<T> {
    protected final DataSource<T> dataSource;
    protected static final Validator validator;

    static {
        try(ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    protected RepositoryImpl(DataSource<T> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public T readById(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.dataSource.findById(id);
    }

    @Override
    public T create(T entity) throws EntityNullReferenceException, EntityValidationException {
        if (entity == null) {
            throw new EntityNullReferenceException();
        }

        validateEntity(entity);

        return this.dataSource.save(entity);
    }

    @Override
    public T update(T entity) throws EntityNullReferenceException, EntityValidationException {
        if (entity == null) {
            throw new EntityNullReferenceException();
        }

        validateEntity(entity);

        return this.dataSource.save(entity);
    }

    @Override
    public Boolean delete(T entity) throws EntityNullReferenceException, KeyNullReferenceException, EntityNotFoundException {
        if (entity == null) {
            throw new EntityNullReferenceException();
        }
        return this.deleteById(entity.getId());
    }

    @Override
    public Boolean deleteById(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.dataSource.delete(id);
    }

    @Override
    public List<T> readAll() {
        return this.dataSource.findAll();
    }

    @Override
    public List<T> readAll(long offset, long limit) {
        return this.dataSource.findAll(offset, limit);
    }

    @Override
    public boolean existsById(Long id) throws KeyNullReferenceException {
        return this.dataSource.existsById(id);
    }

    @Override
    public long count() {
        return this.dataSource.count();
    }

    private void validateEntity(T entity) throws EntityValidationException {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            String messages = violations.stream()
                    .map( cv -> cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage() )
                    .collect( Collectors.joining( ", " ) );

            throw new EntityValidationException(messages);
        }
    }
}
