package com.mjc.school.service.service;

import com.mjc.school.repository.exception.CustomRepositoryException;
import com.mjc.school.repository.exception.EntityNotFoundException;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.repository.Repository;
import com.mjc.school.service.dto.EditNewsRequestDTO;
import com.mjc.school.service.exception.AuthorNotFoundServiceException;
import com.mjc.school.service.exception.CustomServiceException;
import com.mjc.school.service.exception.DTOValidationServiceException;
import com.mjc.school.service.exception.NewsNotFoundServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceUpdateNewsTest {
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
    @DisplayName("Checking the news data submitted for saving to the repository, generated on the basis of an incoming request")
    void update_checkEntityToSave() throws CustomRepositoryException, CustomServiceException {
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "Changed title",
                "Changed contend",
                1L
        );

        Long newsIdToChange = 2L;

        News newsBeforeChange = new News(
                newsIdToChange,
                "Start title",
                "Start content",
                LocalDateTime.of(2024, 4, 16, 14, 33, 3),
                null,
                3L
        );

        News newsAfterChange = new News(
                newsIdToChange,
                requestDTO.getTitle(),
                requestDTO.getContent(),
                newsBeforeChange.getCreateDate(),
                LocalDateTime.of(2024, 4, 16, 14, 37, 31),
                requestDTO.getAuthorId()
        );

        Mockito.when(authorRepository.existsById(requestDTO.getAuthorId())).thenReturn(true);
        Mockito.when(newsRepository.findById(newsIdToChange)).thenReturn(newsBeforeChange);
        Mockito.when(newsRepository.save(Mockito.any(News.class))).thenReturn(newsAfterChange);

        ArgumentCaptor<News> argumentCaptor = ArgumentCaptor.forClass(News.class);

        LocalDateTime updateDateTimeFrom = LocalDateTime.now();
        newsService.update(newsIdToChange, requestDTO);
        LocalDateTime updateDateTimeTo = LocalDateTime.now();

        Mockito.verify(newsRepository).save(argumentCaptor.capture());

        News actualNewsToSave = argumentCaptor.getValue();

        assertThat(actualNewsToSave.getLastUpdateDate()).isBetween(updateDateTimeFrom, updateDateTimeTo);
        newsAfterChange.setLastUpdateDate(actualNewsToSave.getLastUpdateDate());
        assertThat(actualNewsToSave).isEqualTo(newsAfterChange);
    }

    @Test
    @DisplayName("Cases of correctness of news data. No exception should be thrown")
    void correctData_noThrownExceptions() throws CustomRepositoryException {
        Long newsIdForChange = 1L;

        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "12345",
                "54321",
                1L
        );

        News newsBeforeChange = new News(
                newsIdForChange,
                "Start title",
                "Start content",
                LocalDateTime.of(2024, 4, 16, 14, 33, 3),
                null,
                3L
        );

        News newsAfterChange = new News(
                newsIdForChange,
                requestDTO.getTitle(),
                requestDTO.getContent(),
                newsBeforeChange.getCreateDate(),
                LocalDateTime.of(2024, 4, 16, 14, 37, 31),
                requestDTO.getAuthorId()
        );

        Mockito.when(authorRepository.existsById(requestDTO.getAuthorId())).thenReturn(true);
        Mockito.when(newsRepository.findById(newsIdForChange)).thenReturn(newsBeforeChange);
        Mockito.when(newsRepository.save(Mockito.any(News.class))).thenReturn(newsAfterChange);

        assertThatNoException().isThrownBy(() -> newsService.update(newsIdForChange, requestDTO));

        requestDTO.setTitle("123456789012345678901234567890");
        requestDTO.setContent(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345"
        );

        assertThatNoException().isThrownBy(() -> newsService.update(newsIdForChange, requestDTO));
    }

    static Stream<EditNewsRequestDTO> dataForDTOValidateTest() {
        return Stream.of(
                new EditNewsRequestDTO("12", "News content", 2L),
                new EditNewsRequestDTO("", "News content", 2L),
                new EditNewsRequestDTO("1234567890123456789012345678901", "News content", 2L),
                new EditNewsRequestDTO("News title", "123", 2L),
                new EditNewsRequestDTO(
                        "News title",
                        "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"+ // 100 per line
                                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"+
                                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",
                        2L
                )
        );
    }

    @DisplayName("DTOValidationException will be thrown when calling the update method.")
    @ParameterizedTest()
    @MethodSource("dataForDTOValidateTest")
    void update_titleTooShort_throwsDTOValidateException(EditNewsRequestDTO request) {
        assertThatThrownBy(() -> newsService.update(1L, request)).isInstanceOf(DTOValidationServiceException.class);
    }

    @Test
    @DisplayName("When passing an incorrect id for a new author, an AuthorNotFoundException will be thrown")
    void update_notFoundNewAuthor_throwsDTOValidateException() throws CustomRepositoryException {
        Long newsIdForUpdate = 1L;
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                2L
        );
        Mockito.when(authorRepository.existsById(requestDTO.getAuthorId())).thenReturn(false);
        assertThatThrownBy(() -> newsService.update(newsIdForUpdate, requestDTO)).isInstanceOf(AuthorNotFoundServiceException.class);
    }

    @Test
    @DisplayName("When passing the id of a non-existent news, a NewsNotFoundException will be thrown")
    void update_notFoundNewsById_throwsEntityNotFoundException() throws CustomRepositoryException {
        Long newsIdForUpdate = 1L;
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                2L
        );
        Mockito.when(newsRepository.findById(newsIdForUpdate)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> newsService.update(newsIdForUpdate, requestDTO)).isInstanceOf(NewsNotFoundServiceException.class);
    }
}
