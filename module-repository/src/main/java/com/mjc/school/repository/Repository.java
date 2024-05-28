package com.mjc.school.repository;

import com.mjc.school.exception.EntityNotFoundException;
import com.mjc.school.exception.EntityNullReferenceException;
import com.mjc.school.exception.EntityValidationException;
import com.mjc.school.exception.KeyNullReferenceException;
import com.mjc.school.model.Model;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Repository <T extends Model> {
    protected static final Validator validator;

    static {
        try(ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    public abstract T readById(Long id) throws KeyNullReferenceException, EntityNotFoundException;
    public abstract T create(T entity) throws EntityNullReferenceException, EntityValidationException;
    public abstract T update(T entity) throws EntityNullReferenceException, EntityValidationException;
    public abstract Boolean delete(Long id) throws KeyNullReferenceException, EntityNotFoundException;
    public abstract List<T> readAll();

    /**
     * @param offset number of elements to skip. If the value is zero, the elements will be taken from the first element
     * @param limit number of elements no more than the method should return. If the value of "limit" parameter is -1, all the elements of the dataset will be returned
     * @return list of dataset elements
     */
    public abstract List<T> readByPage(long offset, long limit);

    public abstract boolean existsById(Long id) throws KeyNullReferenceException;
    public abstract long count();

    protected void validateEntity(T entity) throws EntityValidationException {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            String messages = violations.stream()
                    .map( cv -> cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage() )
                    .collect( Collectors.joining( ", " ) );

            throw new EntityValidationException(messages);
        }
    }
}
