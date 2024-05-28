package com.mjc.school.datasource;

import com.mjc.school.exception.UnsupportedEntityClassException;
import com.mjc.school.model.AuthorModel;
import com.mjc.school.model.Model;
import com.mjc.school.model.NewsModel;

public class DataSourceFactory {

    private DataSourceFactory() {}

    @SuppressWarnings("unchecked")
    public static <T extends Model> DataSource<T> getDataSource(Class<T> entityClass) {
        if (entityClass == AuthorModel.class) {
            return (DataSource<T>) AuthorDataSource.getInstance();
        } else if (entityClass == NewsModel.class) {
            return (DataSource<T>) NewsDataSource.getInstance();
        }

        throw new UnsupportedEntityClassException(entityClass);
    }
}
