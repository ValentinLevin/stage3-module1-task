package com.mjc.school.repository;

import com.mjc.school.exception.EntityNotFoundException;
import com.mjc.school.exception.EntityNullReferenceException;
import com.mjc.school.exception.EntityValidationException;
import com.mjc.school.exception.KeyNullReferenceException;

import java.util.List;

public interface Repository <T> {
    T readById(Long id) throws KeyNullReferenceException, EntityNotFoundException;
    T create(T entity) throws EntityNullReferenceException, EntityValidationException;
    T update(T entity) throws EntityNullReferenceException, EntityValidationException;
    Boolean delete(T entity) throws EntityNullReferenceException, KeyNullReferenceException, EntityNotFoundException;
    Boolean deleteById(Long id) throws KeyNullReferenceException, EntityNotFoundException;
    List<T> readAll();

    /**
     * @param offset number of elements to skip. If the value is zero, the elements will be taken from the first element
     * @param limit number of elements no more than the method should return. If the value of "limit" parameter is -1, all the elements of the dataset will be returned
     * @return list of dataset elements
     */
    List<T> readAll(long offset, long limit);

    boolean existsById(Long id) throws KeyNullReferenceException;
    long count();
}
