package com.mjc.school.repository.impl;

import com.mjc.school.datasource.DataSource;
import com.mjc.school.exception.CustomRepositoryException;
import com.mjc.school.exception.EntityNullReferenceException;
import com.mjc.school.exception.EntityValidationException;
import com.mjc.school.exception.KeyNullReferenceException;
import com.mjc.school.model.AuthorModel;
import com.mjc.school.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AuthorRepository")
@ExtendWith(MockitoExtension.class)
class AuthorModelRepositoryTest {
    @Mock(strictness = Mock.Strictness.LENIENT)
    private DataSource<AuthorModel> dataSource;
    private Repository<AuthorModel> repository;

    @BeforeEach
    void setup() {
        repository = new AuthorRepository(dataSource);
    }

    @Test
    @DisplayName("When requesting the author by id, the required dataSource method was called")
    void readById_foundEntity() throws CustomRepositoryException {
        AuthorModel expectedAuthorModel = new AuthorModel(1L, "Author 1 name");
        Mockito.when(dataSource.findById(1L)).thenReturn(expectedAuthorModel);

        AuthorModel actualAuthorModel = this.repository.readById(1L);

        assertThat(actualAuthorModel).isEqualTo(expectedAuthorModel);
        Mockito.verify(dataSource, Mockito.only()).findById(1L);
        Mockito.verify(dataSource, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("When a deletion request is made, the required dataSource method will be called")
    void delete_ByEntity() throws CustomRepositoryException {
        AuthorModel authorModelForDelete = new AuthorModel(1L, "Author 1 name");
        Mockito.doReturn(true).when(dataSource).delete(1L);

        this.repository.delete(authorModelForDelete.getId());

        Mockito.verify(dataSource, Mockito.only()).delete(authorModelForDelete.getId());
        Mockito.verify(dataSource, Mockito.times(1)).delete(authorModelForDelete.getId());
    }

    @Test
    @DisplayName("When trying to create a Null object, an EntityNullReferenceException exception will be thrown")
    void create_entityNullReference_throwsEntityNullReferenceException() {
        assertThatThrownBy(() -> repository.create(null)).isInstanceOf(EntityNullReferenceException.class);
    }

    @Test
    @DisplayName("When trying to update a Null object, an EntityNullReferenceException exception will be thrown")
    void update_entityNullReference_throwsEntityNullReferenceException() {
        assertThatThrownBy(() -> repository.update(null)).isInstanceOf(EntityNullReferenceException.class);
    }

    @Test
    @DisplayName("When attempting to query an object using Null as a key, an KeyNullReferenceException exception will be thrown")
    void readById_keyNullReference_throwsKeyNullReferenceException() {
        assertThatThrownBy(() -> repository.readById(null)).isInstanceOf(KeyNullReferenceException.class);
    }

    @Test
    @DisplayName("When passing null as a key to the key deletion method, a KeyNullReferenceException exception will be thrown")
    void delete_ByNullKey_throwsKeyNullReferenceException() {
        assertThatThrownBy(() -> repository.delete(null)).isInstanceOf(KeyNullReferenceException.class);
    }

    @Test
    void create_authorNameTooSmall_throwsEntityValidationException() throws CustomRepositoryException {
        AuthorModel authorModel = new AuthorModel(1L, "12");
        Mockito.doReturn(authorModel).when(dataSource).save(authorModel);
        assertThatThrownBy(() -> repository.create(authorModel)).isInstanceOf(EntityValidationException.class);
    }

    @Test
    void update_authorNameTooSmall_throwsEntityValidationException() throws CustomRepositoryException {
        AuthorModel authorModel = new AuthorModel(1L, "12");
        Mockito.doReturn(authorModel).when(dataSource).save(authorModel);
        assertThatThrownBy(() -> repository.update(authorModel)).isInstanceOf(EntityValidationException.class);
    }
}
