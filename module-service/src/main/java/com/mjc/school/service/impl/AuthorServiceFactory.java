package com.mjc.school.service.impl;

import com.mjc.school.model.AuthorModel;
import com.mjc.school.repository.RepositoryFactory;
import com.mjc.school.service.AuthorService;

public class AuthorServiceFactory {
    private AuthorServiceFactory() {}

    public static AuthorService authorService() {
        return new AuthorServiceImpl(
                RepositoryFactory.getRepository(AuthorModel.class)
        );
    }
}
