package com.mjc.school.repository.repository;

import com.mjc.school.repository.exception.EntityNotFoundException;
import com.mjc.school.repository.exception.EntityNullReferenceException;
import com.mjc.school.repository.exception.EntityValidationException;
import com.mjc.school.repository.exception.KeyNullReferenceException;

import java.util.List;

public interface Repository <T> {
    T findById(Long id) throws KeyNullReferenceException, EntityNotFoundException;
    T save(T entity) throws EntityNullReferenceException, EntityValidationException;
    boolean delete(T entity) throws EntityNullReferenceException, KeyNullReferenceException, EntityNotFoundException;
    boolean deleteById(Long id) throws KeyNullReferenceException, EntityNotFoundException;
    List<T> findAll();

    /**
     * @param offset number of elements to skip. If the value is zero, the elements will be taken from the first element
     * @param limit number of elements no more than the method should return. If the value of "limit" parameter is -1, all the elements of the dataset will be returned
     * @return list of dataset elements
     */
    List<T> findAll(long offset, long limit);

    boolean existsById(Long id) throws KeyNullReferenceException;
    long count();
}
