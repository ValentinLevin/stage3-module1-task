package com.mjc.school.repository.impl;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.datasource.DataSourceFactory;
import com.mjc.school.exception.UnsupportedEntityClassException;
import com.mjc.school.model.Author;
import com.mjc.school.model.Entity;
import com.mjc.school.model.News;
import com.mjc.school.repository.Repository;

public class RepositoryFactory {

    private RepositoryFactory() {}

    @SuppressWarnings("unchecked")
    public static <T extends Entity> Repository<T> getRepository(Class<T> entityClass) {
        if (entityClass == Author.class) {
            DataSource<Author> dataSource = DataSourceFactory.getDataSource(Author.class);
            return (Repository<T>) new AuthorRepository(dataSource);
        } else if (entityClass == News.class) {
            DataSource<News> dataSource = DataSourceFactory.getDataSource(News.class);
            return (Repository<T>) new NewsRepository(dataSource);
        }

        throw new UnsupportedEntityClassException(entityClass);
    }
}
