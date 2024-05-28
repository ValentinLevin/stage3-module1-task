package com.mjc.school.repository.impl;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.exception.EntityNotFoundException;
import com.mjc.school.exception.EntityNullReferenceException;
import com.mjc.school.exception.EntityValidationException;
import com.mjc.school.exception.KeyNullReferenceException;
import com.mjc.school.model.NewsModel;
import com.mjc.school.repository.Repository;

import java.util.List;

public class NewsRepository extends Repository<NewsModel> {
    public NewsRepository(DataSource<NewsModel> dataSource) {
        super(dataSource);
    }

    @Override
    public NewsModel readById(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.dataSource.findById(id);
    }

    @Override
    public NewsModel create(NewsModel entity) throws EntityNullReferenceException, EntityValidationException {
        if (entity == null) {
            throw new EntityNullReferenceException();
        }

        validateEntity(entity);

        return this.dataSource.save(entity);
    }

    @Override
    public NewsModel update(NewsModel entity) throws EntityNullReferenceException, EntityValidationException {
        if (entity == null) {
            throw new EntityNullReferenceException();
        }

        validateEntity(entity);

        return this.dataSource.save(entity);
    }

    @Override
    public Boolean delete(Long id) throws KeyNullReferenceException, EntityNotFoundException {
        if (id == null) {
            throw new KeyNullReferenceException();
        }
        return this.dataSource.delete(id);
    }

    @Override
    public List<NewsModel> readAll() {
        return this.dataSource.findAll();
    }

    @Override
    public List<NewsModel> readAllByPage(long offset, long limit) {
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
