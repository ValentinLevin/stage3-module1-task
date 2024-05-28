package com.mjc.school.datasource;

import com.mjc.school.exception.UnsupportedEntityClassException;
import com.mjc.school.model.Author;
import com.mjc.school.model.Entity;
import com.mjc.school.model.News;

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
