package com.mjc.school.repository.impl;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.exception.EntityNotFoundException;
import com.mjc.school.exception.EntityNullReferenceException;
import com.mjc.school.exception.EntityValidationException;
import com.mjc.school.exception.KeyNullReferenceException;
import com.mjc.school.model.AuthorModel;
import com.mjc.school.repository.Repository;

import java.util.List;

public class AuthorRepository extends Repository<AuthorModel> {
    private final DataSource<AuthorModel> dataSource;

    public AuthorRepository(DataSource<AuthorModel> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AuthorModel readById(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.dataSource.findById(id);
    }

    @Override
    public AuthorModel create(AuthorModel entity) throws EntityNullReferenceException, EntityValidationException {
        if (entity == null) {
            throw new EntityNullReferenceException();
        }

        validateEntity(entity);

        return this.dataSource.save(entity);
    }

    @Override
    public AuthorModel update(AuthorModel entity) throws EntityNullReferenceException, EntityValidationException {
        return this.create(entity);
    }

    @Override
    public Boolean delete(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.dataSource.delete(id);
    }

    @Override
    public List<AuthorModel> readAll() {
        return this.dataSource.findAll();
    }

    @Override
    public List<AuthorModel> readPage(long offset, long limit) {
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
}
