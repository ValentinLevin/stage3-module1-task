package com.mjc.school.repository.repository;

import com.mjc.school.repository.datasource.DataSource;
import com.mjc.school.repository.exception.CustomRepositoryException;
import com.mjc.school.repository.exception.EntityNullReferenceException;
import com.mjc.school.repository.exception.EntityValidationException;
import com.mjc.school.repository.exception.KeyNullReferenceException;
import com.mjc.school.repository.model.News;
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
class NewsRepositoryTest {
    @Mock
    private DataSource<News> dataSource;
    private Repository<News> repository;

    @BeforeEach
    void setup() {
        repository = new NewsRepository(dataSource);
    }

    @Test
    @DisplayName("When requesting news by id, the required dataSource method was called")
    void findById_foundEntity() throws CustomRepositoryException {
        News expectedNews =
                new News(
                        1L,
                        "News 1 name",
                        "News 1 content",
                        LocalDateTime.of(2024, 4, 12, 9, 57, 1),
                        LocalDateTime.of(2024, 4, 12, 12, 14, 37),
                        1L
                );
        Mockito.when(dataSource.findById(1L)).thenReturn(expectedNews);

        News actualNews = this.repository.findById(1L);

        assertThat(actualNews).isEqualTo(expectedNews);
        Mockito.verify(dataSource, Mockito.only()).findById(1L);
        Mockito.verify(dataSource, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("When a deletion request is made, the required dataSource method will be called")
    void delete_ByEntity() throws CustomRepositoryException {
        News newsForDelete =
                new News(
                        1L,
                        "News 1 name",
                        "News 1 content",
                        LocalDateTime.of(2024, 4, 12, 9, 57, 1),
                        LocalDateTime.of(2024, 4, 12, 12, 14, 37),
                        1L
                );
        Mockito.doReturn(true).when(dataSource).delete(1L);

        boolean actualDeleteResult = this.repository.delete(newsForDelete);

        assertThat(actualDeleteResult).isTrue();

        Mockito.verify(dataSource, Mockito.only()).delete(newsForDelete.getId());
        Mockito.verify(dataSource, Mockito.times(1)).delete(newsForDelete.getId());
    }

    @Test
    @DisplayName("When trying to save a Null object, an EntityNullReferenceException exception will be thrown")
    void save_entityNullReference_throwsEntityNullReferenceException() {
        assertThatThrownBy(() -> repository.save(null)).isInstanceOf(EntityNullReferenceException.class);
    }

    @Test
    @DisplayName("When requesting an object with a Null key, an KeyNullReferenceException exception will be thrown")
    void findById_keyNullReference_throwsKeyNullReferenceException() {
        assertThatThrownBy(() -> repository.findById(null)).isInstanceOf(KeyNullReferenceException.class);
    }

    @Test
    @DisplayName("When passing null as an entity to be deleted, an EntityNullReferenceException exception will be thrown to the deletion method")
    void delete_ByNullEntity_throwsEntityNullReferenceException() {
        assertThatThrownBy(() -> repository.delete(null)).isInstanceOf(EntityNullReferenceException.class);
    }

    @Test
    @DisplayName("When passing null as a key to the key deletion method, a KeyNullReferenceException exception will be thrown")
    void delete_ByNullKey_throwsKeyNullReferenceException() {
        assertThatThrownBy(() -> repository.deleteById(null)).isInstanceOf(KeyNullReferenceException.class);
    }

    @Test
    @DisplayName("If the entity field values are incorrect, throw an EntityValidationException")
    void authorTitleAndContentNotValidated_throwsEntityValidationException() {
        News news = new News(
                1L,
                "12",
                "123",
                null,
                null,
                null
        );
        assertThatThrownBy(() -> repository.save(news)).isInstanceOf(EntityValidationException.class);
    }
}
