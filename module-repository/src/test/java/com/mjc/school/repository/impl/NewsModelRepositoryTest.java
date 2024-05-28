package com.mjc.school.repository.impl;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.exception.CustomRepositoryException;
import com.mjc.school.exception.EntityNullReferenceException;
import com.mjc.school.exception.EntityValidationException;
import com.mjc.school.exception.KeyNullReferenceException;
import com.mjc.school.model.NewsModel;
import com.mjc.school.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NewsRepository")
@ExtendWith(MockitoExtension.class)
class NewsModelRepositoryTest {
    @Mock
    private DataSource<NewsModel> dataSource;
    private Repository<NewsModel> repository;

    @BeforeEach
    void setup() {
        repository = new NewsRepository(dataSource);
    }

    @Test
    @DisplayName("When requesting news by id, the required dataSource method was called")
    void readById_foundEntity() throws CustomRepositoryException {
        NewsModel expectedNewsModel =
                new NewsModel(
                        1L,
                        "News 1 name",
                        "News 1 content",
                        LocalDateTime.of(2024, 4, 12, 9, 57, 1),
                        LocalDateTime.of(2024, 4, 12, 12, 14, 37),
                        1L
                );
        Mockito.when(dataSource.findById(1L)).thenReturn(expectedNewsModel);

        NewsModel actualNewsModel = this.repository.readById(1L);

        assertThat(actualNewsModel).isEqualTo(expectedNewsModel);
        Mockito.verify(dataSource, Mockito.only()).findById(1L);
        Mockito.verify(dataSource, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("When a deletion request is made, the required dataSource method will be called")
    void delete_ByEntity() throws CustomRepositoryException {
        NewsModel newsModelForDelete =
                new NewsModel(
                        1L,
                        "News 1 name",
                        "News 1 content",
                        LocalDateTime.of(2024, 4, 12, 9, 57, 1),
                        LocalDateTime.of(2024, 4, 12, 12, 14, 37),
                        1L
                );
        Mockito.doReturn(true).when(dataSource).delete(1L);

        boolean actualDeleteResult = this.repository.delete(newsModelForDelete.getId());

        assertThat(actualDeleteResult).isTrue();

        Mockito.verify(dataSource, Mockito.only()).delete(newsModelForDelete.getId());
        Mockito.verify(dataSource, Mockito.times(1)).delete(newsModelForDelete.getId());
    }

    @Test
    @DisplayName("When trying to create a Null object, an EntityNullReferenceException exception will be thrown")
    void create_entityNullReference_throwsEntityNullReferenceException() {
        assertThatThrownBy(() -> repository.create(null)).isInstanceOf(EntityNullReferenceException.class);
    }

    @Test
    @DisplayName("When trying to create a Null object, an EntityNullReferenceException exception will be thrown")
    void update_entityNullReference_throwsEntityNullReferenceException() {
        assertThatThrownBy(() -> repository.update(null)).isInstanceOf(EntityNullReferenceException.class);
    }

    @Test
    @DisplayName("When requesting an object with a Null key, an KeyNullReferenceException exception will be thrown")
    void readById_keyNullReference_throwsKeyNullReferenceException() {
        assertThatThrownBy(() -> repository.readById(null)).isInstanceOf(KeyNullReferenceException.class);
    }

    @Test
    @DisplayName("When passing null as a key to the key deletion method, a KeyNullReferenceException exception will be thrown")
    void delete_ByNullKey_throwsKeyNullReferenceException() {
        assertThatThrownBy(() -> repository.delete(null)).isInstanceOf(KeyNullReferenceException.class);
    }

    @Test
    @DisplayName("If the entity field values are incorrect, throw an EntityValidationException")
    void create_authorTitleAndContentNotValidated_throwsEntityValidationException() {
        NewsModel newsModel = new NewsModel(
                0L,
                "12",
                "123",
                null,
                null,
                null
        );
        assertThatThrownBy(() -> repository.create(newsModel)).isInstanceOf(EntityValidationException.class);
    }

    @Test
    @DisplayName("If the entity field values are incorrect, throw an EntityValidationException")
    void update_authorTitleAndContentNotValidated_throwsEntityValidationException() {
        NewsModel newsModel = new NewsModel(
                1L,
                "12",
                "123",
                null,
                null,
                null
        );
        assertThatThrownBy(() -> repository.update(newsModel)).isInstanceOf(EntityValidationException.class);
    }
}
