package com.mjc.school.repository.datasource;

import com.mjc.school.repository.exception.UnsupportedEntityClassException;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.Entity;
import com.mjc.school.repository.model.News;

public class DataSourceFactory {

    private DataSourceFactory() {}

    @SuppressWarnings("unchecked")
    public static <T extends Entity> DataSource<T> getDataSource(Class<T> entityClass) {
        if (entityClass == Author.class) {
            return (DataSource<T>) AuthorDataSource.getInstance();
        } else if (entityClass == News.class) {
            return (DataSource<T>) NewsDataSource.getInstance();
        }

        throw new UnsupportedEntityClassException(entityClass);
    }
}
