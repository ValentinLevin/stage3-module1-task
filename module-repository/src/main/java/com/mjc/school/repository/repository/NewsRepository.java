package com.mjc.school.repository.repository;

import com.mjc.school.repository.datasource.DataSource;
import com.mjc.school.repository.model.News;

class NewsRepository extends RepositoryImpl<News> implements Repository<News> {
    public NewsRepository(DataSource<News> dataSource) {
        super(dataSource);
    }
}
