package com.mjc.school.datasource;

import com.mjc.school.model.NewsModel;

class NewsDataSource extends DataSourceImpl<NewsModel> implements DataSource<NewsModel> {
    private static final String DATA_FILE_NAME = "news.json";

    private NewsDataSource() {
        super(DATA_FILE_NAME, NewsModel.class);
    }

    private static class SingletonCreationHelper {
        private static final DataSource<NewsModel> INSTANCE = new NewsDataSource();
    }

    public static DataSource<NewsModel> getInstance() {
        return SingletonCreationHelper.INSTANCE;
    }
}
