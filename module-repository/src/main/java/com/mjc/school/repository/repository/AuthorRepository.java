package com.mjc.school.repository.repository;

import com.mjc.school.repository.datasource.DataSource;
import com.mjc.school.repository.model.Author;

class AuthorRepository extends RepositoryImpl<Author> implements Repository<Author> {
    public AuthorRepository(DataSource<Author> dataSource) {
        super(dataSource);
    }
}
