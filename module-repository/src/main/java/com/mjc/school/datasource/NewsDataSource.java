package com.mjc.school.datasource;

import com.mjc.school.model.News;

class NewsDataSource extends DataSourceImpl<News> implements DataSource<News> {
    private static final String DATA_FILE_NAME = "news.json";

    private NewsDataSource() {
        super(DATA_FILE_NAME, News.class);
    }

    private static class SingletonCreationHelper {
        private static final DataSource<News> INSTANCE = new NewsDataSource();
    }

    public static DataSource<News> getInstance() {
        return SingletonCreationHelper.INSTANCE;
    }
}
