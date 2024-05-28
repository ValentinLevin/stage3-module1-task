package com.mjc.school.repository.impl;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.model.News;
import com.mjc.school.repository.Repository;

class NewsRepository extends RepositoryImpl<News> implements Repository<News> {
    public NewsRepository(DataSource<News> dataSource) {
        super(dataSource);
    }
}
