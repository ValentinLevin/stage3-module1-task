package com.mjc.school.service.impl;

import com.mjc.school.model.Author;
import com.mjc.school.model.News;
import com.mjc.school.repository.impl.RepositoryFactory;
import com.mjc.school.service.NewsService;

public class NewsServiceFactory {
    private NewsServiceFactory() {}

    public static NewsService newsService() {
        return new NewsServiceImpl(
                RepositoryFactory.getRepository(News.class),
                RepositoryFactory.getRepository(Author.class)
        );
    }
}
