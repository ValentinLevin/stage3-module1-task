package com.mjc.school.service.service;

import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.repository.RepositoryFactory;

public class NewsServiceFactory {
    private NewsServiceFactory() {}

    public static NewsService newsService() {
        return new NewsServiceImpl(
                RepositoryFactory.getRepository(News.class),
                RepositoryFactory.getRepository(Author.class)
        );
    }
}
