package com.mjc.school.datasource;

import com.mjc.school.model.AuthorModel;

class AuthorDataSource extends DataSourceImpl<AuthorModel> {
    private static final String DATA_FILE_NAME = "author.json";

    private AuthorDataSource() {
        super(DATA_FILE_NAME, AuthorModel.class);
    }

    private static class SingletonCreationHelper {
        private static final DataSource<AuthorModel> INSTANCE = new AuthorDataSource();
    }

    public static DataSource<AuthorModel> getInstance() {
        return SingletonCreationHelper.INSTANCE;
    }
}
