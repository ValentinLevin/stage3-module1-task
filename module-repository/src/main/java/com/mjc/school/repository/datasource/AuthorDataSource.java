package com.mjc.school.repository.datasource;

import com.mjc.school.repository.model.Author;

class AuthorDataSource extends DataSourceImpl<Author> {
    private static final String DATA_FILE_NAME = "author.json";

    private AuthorDataSource() {
        super(DATA_FILE_NAME, Author.class);
    }

    private static class SingletonCreationHelper {
        private static final DataSource<Author> INSTANCE = new AuthorDataSource();
    }

    public static DataSource<Author> getInstance() {
        return SingletonCreationHelper.INSTANCE;
    }
}
