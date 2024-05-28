package com.mjc.school.repository;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.datasource.DataSourceFactory;
import com.mjc.school.exception.UnsupportedEntityClassException;
import com.mjc.school.model.AuthorModel;
import com.mjc.school.model.Model;
import com.mjc.school.model.NewsModel;
import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.repository.impl.NewsRepository;

public class RepositoryFactory {

    private RepositoryFactory() {}

    @SuppressWarnings("unchecked")
    public static <T extends Model> Repository<T> getRepository(Class<T> entityClass) {
        if (entityClass == AuthorModel.class) {
            DataSource<AuthorModel> dataSource = DataSourceFactory.getDataSource(AuthorModel.class);
            return (Repository<T>) new AuthorRepository(dataSource);
        } else if (entityClass == NewsModel.class) {
            DataSource<NewsModel> dataSource = DataSourceFactory.getDataSource(NewsModel.class);
            return (Repository<T>) new NewsRepository(dataSource);
        }

        throw new UnsupportedEntityClassException(entityClass);
    }
}
