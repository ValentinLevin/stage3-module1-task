package com.mjc.school.repository.repository;

import com.mjc.school.repository.datasource.DataSource;
import com.mjc.school.repository.exception.EntityNotFoundException;
import com.mjc.school.repository.exception.EntityNullReferenceException;
import com.mjc.school.repository.exception.EntityValidationException;
import com.mjc.school.repository.exception.KeyNullReferenceException;
import com.mjc.school.repository.model.Entity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RepositoryImpl<T extends Entity> implements Repository<T>{
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
    public T findById(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.dataSource.findById(id);
    }

    @Override
    public T save(T entity) throws EntityNullReferenceException, EntityValidationException {
        if (entity == null) {
            throw new EntityNullReferenceException();
        }

        validateEntity(entity);

        return this.dataSource.save(entity);
    }

    @Override
    public boolean delete(T entity) throws EntityNullReferenceException, KeyNullReferenceException, EntityNotFoundException {
        if (entity == null) {
            throw new EntityNullReferenceException();
        }
        return this.deleteById(entity.getId());
    }

    @Override
    public boolean deleteById(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.dataSource.delete(id);
    }

    @Override
    public List<T> findAll() {
        return this.dataSource.findAll();
    }

    @Override
    public List<T> findAll(long offset, long limit) {
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
