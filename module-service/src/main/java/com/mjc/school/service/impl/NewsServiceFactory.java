package com.mjc.school.service.impl;

import com.mjc.school.model.AuthorModel;
import com.mjc.school.model.NewsModel;
import com.mjc.school.repository.RepositoryFactory;
import com.mjc.school.service.NewsService;

public class NewsServiceFactory {
    private NewsServiceFactory() {}

    public static NewsService newsService() {
        return new NewsServiceImpl(
                RepositoryFactory.getRepository(NewsModel.class),
                RepositoryFactory.getRepository(AuthorModel.class)
        );
    }
}
