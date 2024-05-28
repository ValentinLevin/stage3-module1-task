package com.mjc.school.service.impl;

import com.mjc.school.dto.EditNewsRequestDTO;
import com.mjc.school.exception.*;
import com.mjc.school.model.NewsModel;
import com.mjc.school.repository.Repository;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.NewsService;
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
class NewsServiceUpdateNewsModelTest {
    @Mock
    private AuthorService authorService;

    @Mock
    private Repository<NewsModel> newsRepository;

    private NewsService newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(newsRepository, authorService);
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

        requestDTO.setId(newsIdToChange);

        NewsModel newsModelBeforeChange = new NewsModel(
                newsIdToChange,
                "Start title",
                "Start content",
                LocalDateTime.of(2024, 4, 16, 14, 33, 3),
                null,
                3L
        );

        NewsModel newsModelAfterChange = new NewsModel(
                newsIdToChange,
                requestDTO.getTitle(),
                requestDTO.getContent(),
                newsModelBeforeChange.getCreateDate(),
                LocalDateTime.of(2024, 4, 16, 14, 37, 31),
                requestDTO.getAuthorId()
        );

        Mockito.when(authorService.existsById(requestDTO.getAuthorId())).thenReturn(true);
        Mockito.when(newsRepository.readById(newsIdToChange)).thenReturn(newsModelBeforeChange);
        Mockito.when(newsRepository.update(Mockito.any(NewsModel.class))).thenReturn(newsModelAfterChange);

        ArgumentCaptor<NewsModel> argumentCaptor = ArgumentCaptor.forClass(NewsModel.class);

        LocalDateTime updateDateTimeFrom = LocalDateTime.now();
        newsService.update(requestDTO);
        LocalDateTime updateDateTimeTo = LocalDateTime.now();

        Mockito.verify(newsRepository).update(argumentCaptor.capture());

        NewsModel actualNewsModelToSave = argumentCaptor.getValue();

        assertThat(actualNewsModelToSave.getLastUpdateDate()).isBetween(updateDateTimeFrom, updateDateTimeTo);
        newsModelAfterChange.setLastUpdateDate(actualNewsModelToSave.getLastUpdateDate());
        assertThat(actualNewsModelToSave).isEqualTo(newsModelAfterChange);
    }

    @Test
    @DisplayName("Cases of correctness of news data. No exception should be thrown")
    void correctData_noThrownExceptions() throws CustomRepositoryException, CustomServiceException {
        Long newsIdForChange = 1L;

        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "12345",
                "54321",
                1L
        );

        requestDTO.setId(newsIdForChange);

        NewsModel newsModelBeforeChange = new NewsModel(
                newsIdForChange,
                "Start title",
                "Start content",
                LocalDateTime.of(2024, 4, 16, 14, 33, 3),
                null,
                3L
        );

        NewsModel newsModelAfterChange = new NewsModel(
                newsIdForChange,
                requestDTO.getTitle(),
                requestDTO.getContent(),
                newsModelBeforeChange.getCreateDate(),
                LocalDateTime.of(2024, 4, 16, 14, 37, 31),
                requestDTO.getAuthorId()
        );

        Mockito.when(authorService.existsById(requestDTO.getAuthorId())).thenReturn(true);
        Mockito.when(newsRepository.readById(newsIdForChange)).thenReturn(newsModelBeforeChange);
        Mockito.when(newsRepository.update(Mockito.any(NewsModel.class))).thenReturn(newsModelAfterChange);

        assertThatNoException().isThrownBy(() -> newsService.update(requestDTO));

        requestDTO.setTitle("123456789012345678901234567890");
        requestDTO.setContent(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345"
        );

        assertThatNoException().isThrownBy(() -> newsService.update(requestDTO));
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
        assertThatThrownBy(() -> newsService.update(request)).isInstanceOf(DTOValidationServiceException.class);
    }

    @Test
    @DisplayName("When passing an incorrect id for a new author, an AuthorNotFoundException will be thrown")
    void update_notFoundNewAuthor_throwsDTOValidateException() throws CustomServiceException {
        Long newsIdForUpdate = 1L;
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                2L
        );
        requestDTO.setId(newsIdForUpdate);
        Mockito.when(authorService.existsById(requestDTO.getAuthorId())).thenReturn(false);
        assertThatThrownBy(() -> newsService.update(requestDTO)).isInstanceOf(AuthorNotFoundServiceException.class);
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
        requestDTO.setId(newsIdForUpdate);
        Mockito.when(newsRepository.readById(newsIdForUpdate)).thenThrow(EntityNotFoundException.class);
        assertThatThrownBy(() -> newsService.update(requestDTO)).isInstanceOf(NewsNotFoundServiceException.class);
    }
}
