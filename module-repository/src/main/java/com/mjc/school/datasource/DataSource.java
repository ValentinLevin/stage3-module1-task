package com.mjc.school.datasource;

import com.mjc.school.exception.EntityNotFoundException;
import com.mjc.school.exception.EntityNullReferenceException;
import com.mjc.school.exception.KeyNullReferenceException;
import com.mjc.school.model.Model;

import java.util.List;

public interface DataSource <T extends Model> {
    T findById(Long id) throws KeyNullReferenceException, EntityNotFoundException;
    List<T> findAll();

    /**
     * @param offset number of elements to skip. If the value is zero, the elements will be taken from the first element
     * @param limit number of elements no more than the method should return. If the value of "limit" parameter is -1, all the elements of the dataset will be returned
     * @return list of dataset elements
     */
    List<T> findAll(long offset, long limit);

    T save(T value) throws EntityNullReferenceException;
    boolean delete(Long id) throws KeyNullReferenceException, EntityNotFoundException;
    long count();
    boolean existsById(Long id) throws KeyNullReferenceException;
}
