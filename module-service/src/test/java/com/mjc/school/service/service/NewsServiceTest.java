package com.mjc.school.service.service;

import com.mjc.school.repository.exception.CustomRepositoryException;
import com.mjc.school.repository.exception.EntityNotFoundException;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.repository.Repository;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.service.exception.CustomServiceException;
import com.mjc.school.service.exception.NewsNotFoundServiceException;
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
class NewsServiceTest {
    @Mock
    private Repository<Author> authorRepository;

    @Mock
    private Repository<News> newsRepository;

    private NewsService newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(newsRepository, authorRepository);
    }

    @Test
    @DisplayName("Checking the response to a single news request")
    void findById_exists_true() throws CustomServiceException, CustomRepositoryException {
        Author author = new Author(1L, "Author 1 name");
        News news =
                new News(
                        1L,
                        "News 1 title",
                        "News 1 content",
                        LocalDateTime.of(2024, 4, 14, 17, 10, 12),
                        null,
                        1L
                );

        Mockito.doReturn(author).when(authorRepository).findById(1L);
        Mockito.doReturn(news).when(newsRepository).findById(1L);

        NewsDTO expectedNewsDTO =
                new NewsDTO(
                        news.getId(),
                        news.getTitle(),
                        news.getContent(),
                        "2024-04-14T17:10:12",
                        null,
                        new AuthorDTO(author.getId(), author.getName())
                );

        NewsDTO actualNewsDTO = newsService.findById(1L);

        assertThat(actualNewsDTO).isEqualTo(expectedNewsDTO);
    }

    @Test
    @DisplayName("If you specify an incorrect news id, a NewsNotFoundException will be thrown")
    void findById_exists_false() throws CustomRepositoryException {
        Mockito.when(newsRepository.findById(1L)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> newsService.findById(1L)).isInstanceOf(NewsNotFoundServiceException.class);
    }

    @Test
    @DisplayName("The correct ID was sent. Execution without errors")
    void deleteById_exists() throws CustomRepositoryException, CustomServiceException {
        Long idForDelete = 1L;
        boolean expectedDeleteResult = true;

        Mockito.when(newsRepository.deleteById(idForDelete)).thenReturn(expectedDeleteResult);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        boolean actualDeleteResult = newsService.deleteById(idForDelete);

        Mockito.verify(newsRepository).deleteById(argumentCaptor.capture());

        Long actualId = argumentCaptor.getValue();

        assertThat(actualId).isEqualTo(idForDelete);
        assertThat(actualDeleteResult).isEqualTo(expectedDeleteResult);
    }

    @Test
    @DisplayName("The incorrect ID was sent. The delete method will throw NewsNotFoundException")
    void deleteById_notExists() throws CustomRepositoryException {
        long idForDelete = 1L;
        Mockito.when(newsRepository.deleteById(idForDelete)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> newsService.deleteById(idForDelete)).isInstanceOf(NewsNotFoundServiceException.class);
    }
}
