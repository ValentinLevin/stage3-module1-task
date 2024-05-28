package com.mjc.school.repository.datasource;

import com.mjc.school.repository.exception.CustomRepositoryException;
import com.mjc.school.repository.exception.EntityNotFoundException;
import com.mjc.school.repository.model.News;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NewsDataSource")
class NewsDataSourceTest {
    private static final DataSource<News> dataSource = DataSourceFactory.getDataSource(News.class);
    private static final List<News> readItems = dataSource.findAll();

    private Long findRandomId() {
        Random random = new Random(System.currentTimeMillis());
        List<News> news = dataSource.findAll();
        return news.get(random.nextInt(news.size())).getId();
    }

    @Test
    @DisplayName("All news data has been read")
    void readDataFromFile_allDataHasBeenLoaded_firstNewsDataIsEqualsToExpected() {
        assertThat(readItems).hasSize(25);

        List<Long> expectedIdList = new ArrayList<>();
        LongStream.range(1, 26).forEach(expectedIdList::add);
        assertThat(readItems)
                .extracting("id")
                .containsExactlyElementsOf(expectedIdList);

        News expectedNews =
                new News(
                        1L,
                "News 1 title",
                "News 1 content",
                LocalDateTime.of(2024, 4, 5, 14, 12, 31),
                LocalDateTime.of(2024, 5, 1, 9, 25, 1),
                2L
                );

        Optional<News> actualNews = readItems.stream().filter(item -> item.getId() == 1L).findFirst();

        assertThat(actualNews).isPresent().contains(expectedNews);
    }

    @Test
    @DisplayName("When an entity is deleted, it is deleted from the dataSource")
    void delete_checkNotExistsInDataSource() throws CustomRepositoryException {
        Long idToDelete = findRandomId();

        dataSource.findById(idToDelete);
        long expectedCount = dataSource.count() - 1;

        assertThat(dataSource.delete(idToDelete)).isTrue();
        assertThat(dataSource.count()).isEqualTo(expectedCount);

        assertThatThrownBy(() -> dataSource.delete(idToDelete)).isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> dataSource.findById(idToDelete)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("When an entity is added, it is saved in the dataset")
    void add_checkExistsInDataSource() throws CustomRepositoryException {
        News expectedEntity = new News();

        expectedEntity.setTitle("New title");
        expectedEntity.setContent("New content");
        expectedEntity.setCreateDate(LocalDateTime.now());
        expectedEntity.setLastUpdateDate(expectedEntity.getCreateDate());
        expectedEntity.setAuthorId(1L);

        long expectedItemCount = dataSource.count() + 1;

        News addedEntity = dataSource.save(expectedEntity);

        assertThat(dataSource.count()).isEqualTo(expectedItemCount);
        assertThat(addedEntity.getId()).isNotZero();

        expectedEntity.setId(addedEntity.getId());

        News fetchedEntity = dataSource.findById(addedEntity.getId());
        assertThat(fetchedEntity).isNotSameAs(expectedEntity).isEqualTo(expectedEntity);
    }

    @Test
    @DisplayName("When the data is changed, the changes are saved in the dataset")
    void save_update_savedEntityIsEqualsToFetchedEntity_true() throws CustomRepositoryException {
        long idToChange = findRandomId();
        News expectedEntity = dataSource.findById(idToChange);
        expectedEntity.setTitle("Changed title");
        expectedEntity.setContent("Changed content");
        expectedEntity.setAuthorId(999L);

        long expectedCount = dataSource.count();

        News savedEntity = dataSource.save(expectedEntity);
        News actualEntity = dataSource.findById(savedEntity.getId());

        long actualCount = dataSource.count();

        assertThat(actualCount).isEqualTo(expectedCount);
        assertThat(actualEntity)
                .isNotSameAs(savedEntity)
                .isEqualTo(savedEntity)

                .isNotSameAs(expectedEntity)
                .isEqualTo(expectedEntity);
    }

    @Test
    @DisplayName("When changing the data of an entity issued by a dataSource, the entity data in the dataSource does not change")
    void save_update_OnSavingAndFetchingNewInstanceOfEntityCreatedNotDependOfEachOther_true() throws CustomRepositoryException {
        Long idForCheck = findRandomId();

        News firstFetchEntity = dataSource.findById(idForCheck);
        News secondFetchEntity_ForChange = dataSource.findById(idForCheck);
        secondFetchEntity_ForChange.setTitle("Change title");
        News actualEntity = dataSource.findById(idForCheck);

        assertThat(actualEntity)
                .isNotSameAs(firstFetchEntity)
                .isEqualTo(firstFetchEntity)

                .isNotSameAs(secondFetchEntity_ForChange)
                .isNotEqualTo(secondFetchEntity_ForChange);
    }

    @Test
    @DisplayName("When trying to search by an existing id, an entity with the corresponding id will be returned")
    void findById_found() throws CustomRepositoryException {
        Long idForFetch = findRandomId();
        News news = dataSource.findById(idForFetch);
        assertThat(news).isNotNull().extracting("id").isEqualTo(news.getId());
    }

    @Test
    @DisplayName("If you try to search on a non-existent ID, an exception EntityNotFoundException is thrown")
    void findById_notFound_nullAsResult() {
        assertThatThrownBy(() -> dataSource.findById(-1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("findAll with assigned limit parameter returns a list of items with a size equal to the limit value")
    void testLimit() {
        int offset = 0;
        int limit  = 1;

        List<News> actualItems = dataSource.findAll(offset, limit);

        assertThat(actualItems).hasSize(limit);

        limit  = 2;

        actualItems = dataSource.findAll(offset, limit);

        assertThat(actualItems).hasSize(limit);
    }

    @Test
    @DisplayName("If the offset is 2 and the limit is 5, then findAll returns exactly elements with indexes 2-6")
    void test() {
        List<News> allItems = dataSource.findAll();

        List<News> expectedItems =
                List.of(
                    allItems.get(2),
                    allItems.get(3),
                    allItems.get(4),
                    allItems.get(5),
                    allItems.get(6)
                );

        int offset = 2;
        int limit  = 5;

        List<News> actualItems = dataSource.findAll(offset, limit);

        assertThat(actualItems).containsExactlyElementsOf(expectedItems);
    }

    @Test
    @DisplayName("When the limit value is -1, the findAll method returns all elements after the element with index equal to offset value")
    void testWithNegativeLimitValue() {
        List<News> expectedItems = new ArrayList<>(dataSource.findAll());
        expectedItems.remove(0);
        expectedItems.remove(0);

        int offset = 2;
        int limit = -1;

        List<News> actualItems = dataSource.findAll(offset, limit);

        assertThat(actualItems).containsExactlyElementsOf(expectedItems);
    }

    @Test
    @DisplayName("When the offset value is equal of higher that total element count, the findAll method returns empty list")
    void testWithTooMuchOffsetValue() {
        long offset = dataSource.count();
        List<News> actualItems = dataSource.findAll(offset, 1);
        assertThat(actualItems).isEmpty();
    }
}
