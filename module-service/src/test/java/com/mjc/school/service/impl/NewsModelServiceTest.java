package com.mjc.school.service.impl;

import com.mjc.school.exception.CustomRepositoryException;
import com.mjc.school.exception.EntityNotFoundException;
import com.mjc.school.model.AuthorModel;
import com.mjc.school.model.NewsModel;
import com.mjc.school.repository.Repository;
import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.dto.NewsDTO;
import com.mjc.school.exception.CustomServiceException;
import com.mjc.school.exception.NewsNotFoundServiceException;
import com.mjc.school.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class NewsModelServiceTest {
    @Mock
    private Repository<AuthorModel> authorRepository;

    @Mock
    private Repository<NewsModel> newsRepository;

    private NewsService newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(newsRepository, authorRepository);
    }

    @Test
    @DisplayName("Checking the response to a single news request")
    void readById_exists_true() throws CustomServiceException, CustomRepositoryException {
        AuthorModel authorModel = new AuthorModel(1L, "Author 1 name");
        NewsModel newsModel =
                new NewsModel(
                        1L,
                        "News 1 title",
                        "News 1 content",
                        LocalDateTime.of(2024, 4, 14, 17, 10, 12),
                        null,
                        1L
                );

        Mockito.doReturn(authorModel).when(authorRepository).readById(1L);
        Mockito.doReturn(newsModel).when(newsRepository).readById(1L);

        NewsDTO expectedNewsDTO =
                new NewsDTO(
                        newsModel.getId(),
                        newsModel.getTitle(),
                        newsModel.getContent(),
                        "2024-04-14T17:10:12",
                        null,
                        new AuthorDTO(authorModel.getId(), authorModel.getName())
                );

        NewsDTO actualNewsDTO = newsService.readById(1L);

        assertThat(actualNewsDTO).isEqualTo(expectedNewsDTO);
    }

    @Test
    @DisplayName("If you specify an incorrect news id, a NewsNotFoundException will be thrown")
    void readById_exists_false() throws CustomRepositoryException {
        Mockito.when(newsRepository.readById(1L)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> newsService.readById(1L)).isInstanceOf(NewsNotFoundServiceException.class);
    }

    @Test
    @DisplayName("The correct ID was sent. Execution without errors")
    void deleteById_exists() throws CustomRepositoryException, CustomServiceException {
        Long idForDelete = 1L;
        boolean expectedDeleteResult = true;

        Mockito.when(newsRepository.delete(idForDelete)).thenReturn(expectedDeleteResult);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        boolean actualDeleteResult = newsService.deleteById(idForDelete);

        Mockito.verify(newsRepository).delete(argumentCaptor.capture());

        Long actualId = argumentCaptor.getValue();

        assertThat(actualId).isEqualTo(idForDelete);
        assertThat(actualDeleteResult).isEqualTo(expectedDeleteResult);
    }

    @Test
    @DisplayName("The incorrect ID was sent. The delete method will throw NewsNotFoundException")
    void deleteById_notExists() throws CustomRepositoryException {
        long idForDelete = 1L;
        Mockito.when(newsRepository.delete(idForDelete)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> newsService.deleteById(idForDelete)).isInstanceOf(NewsNotFoundServiceException.class);
    }
}
