package com.mjc.school.repository.impl;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.model.Author;
import com.mjc.school.repository.Repository;

class AuthorRepository extends RepositoryImpl<Author> implements Repository<Author> {
    public AuthorRepository(DataSource<Author> dataSource) {
        super(dataSource);
    }
}
