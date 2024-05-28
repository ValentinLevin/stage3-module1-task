package com.mjc.school.repository.repository;

import com.mjc.school.repository.datasource.DataSource;
import com.mjc.school.repository.datasource.DataSourceFactory;
import com.mjc.school.repository.exception.UnsupportedEntityClassException;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.Entity;
import com.mjc.school.repository.model.News;

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
