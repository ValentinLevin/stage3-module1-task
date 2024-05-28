package com.mjc.school.repository.datasource;

import com.mjc.school.repository.exception.CustomRepositoryException;
import com.mjc.school.repository.exception.EntityNotFoundException;
import com.mjc.school.repository.model.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@DisplayName("AuthorDataSource")
class AuthorDataSourceTest {
    private static final DataSource<Author> dataSource = DataSourceFactory.getDataSource(Author.class);
    private static final List<Author> readItems = dataSource.findAll();

    private Long findRandomId() {
        Random random = new Random(System.currentTimeMillis());
        List<Author> authors = dataSource.findAll();
        return authors.get(random.nextInt(authors.size())).getId();
    }

    @Test
    @DisplayName("The data from the file was read correctly")
    void readDataFromFile_firstAuthorDataHasBeenLoaded_True() {
        Author expectedAuthorData = new Author(1L, "Author 1 name");
        Optional<Author> actualAuthorData = readItems.stream().filter(item -> item.getId() == 1L).findFirst();
        assertThat(actualAuthorData).isPresent().contains(expectedAuthorData);
        assertThat(readItems)
                .extracting("id", "name")
                .containsOnly(tuple(1L, "Author 1 name"), tuple(2L, "Author 2 name"), tuple(3L, "Author 3 name"));
    }

    @Test
    @DisplayName("When deleting one entity, only one entity was deleted")
    void delete_EntityHasBeenRemovedFromDataSource_true() throws CustomRepositoryException {
        long expectedAuthorCountAfterDelete = dataSource.count()-1;

        Long idToDelete = findRandomId();
        dataSource.delete(idToDelete);

        assertThat(dataSource.count()).isEqualTo(expectedAuthorCountAfterDelete);
        assertThatThrownBy(() -> dataSource.findById(idToDelete)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When deleting an entity, it was the selected entity that was deleted")
    void delete_SelectedEntityHasBeenRemoved_true() throws CustomRepositoryException {
        long idToDelete = findRandomId();
        dataSource.delete(idToDelete);
        assertThatThrownBy(() -> dataSource.findById(idToDelete)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("After adding a new entity, the number of records increased by 1")
    void save_add_NumberOfRecordIncreasedByOneAfterAddingTheEntity_true() throws CustomRepositoryException {
        long expectedAuthorCount = dataSource.count()+1;

        Author newAuthor = new Author("New Author");
        dataSource.save(newAuthor);

        assertThat(dataSource.count()).isEqualTo(expectedAuthorCount);
    }

    @Test
    @DisplayName("After adding a new entity, the author's data obtained from the dataSource is equal to the added data")
    void save_add_newAuthorHasBeenAddedIntoDataSource() throws CustomRepositoryException {
        Author newAuthor = new Author("New Author");
        Author addedAuthor = dataSource.save(newAuthor);
        Author newAuthorDataReceivedFromDataSource = dataSource.findById(addedAuthor.getId());

        assertThat(newAuthor.getName()).isEqualTo(newAuthorDataReceivedFromDataSource.getName());
    }

    @Test
    @DisplayName("When changing the data of an entity issued by a dataSource, the entity data in the dataSource does not change")
    void save_update_OnSavingAndFetchingNewInstanceOfEntityCreatedNotDependOfEachOther_true() throws CustomRepositoryException {
        Long idForCheck = findRandomId();

        Author firstFetchEntity = dataSource.findById(idForCheck);
        Author secondFetchEntity_ForChange = dataSource.findById(idForCheck);

        secondFetchEntity_ForChange.setName("Change author name");

        Author actualEntity = dataSource.findById(idForCheck);

        assertThat(actualEntity)
                .isNotSameAs(firstFetchEntity)
                .isEqualTo(firstFetchEntity)

                .isNotSameAs(secondFetchEntity_ForChange)
                .isNotEqualTo(secondFetchEntity_ForChange);
    }

    @Test
    @DisplayName("After saving the changes to the entity in the dataSource, the data is returned changed when the request is repeated")
    void save_update_savedEntityIsEqualsToFetchedEntity_true() throws CustomRepositoryException {
        Long idForFetch = findRandomId();
        Author expectedAuthor = dataSource.findById(idForFetch);

        expectedAuthor.setName("Changed author name");

        dataSource.save(expectedAuthor);

        Author actualAuthor = dataSource.findById(expectedAuthor.getId());

        assertThat(actualAuthor)
                .isNotSameAs(expectedAuthor)
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("If you try to search by a non-existent id, null will be returned")
    void findById_notFound_nullAsResult() {
        assertThatThrownBy(() -> dataSource.findById(-1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("If you try to search for an existing id, an entity with the corresponding id will be returned")
    void findById_found() throws CustomRepositoryException {
        Long idForFetch = findRandomId();
        Author author = dataSource.findById(idForFetch);
        assertThat(author).isNotNull().extracting("id").isEqualTo(author.getId());
    }

    @Test
    @DisplayName("When checking for the presence of an entity by an existing id, it will return true")
    void existsById_exists_true() throws CustomRepositoryException {
        long idForCheck = findRandomId();
        assertThat(dataSource.existsById(idForCheck)).isTrue();
    }
}
