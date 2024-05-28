package com.mjc.school.service.impl;

import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.dto.EditNewsRequestDTO;
import com.mjc.school.exception.AuthorNotFoundServiceException;
import com.mjc.school.exception.CustomRepositoryException;
import com.mjc.school.exception.CustomServiceException;
import com.mjc.school.exception.DTOValidationServiceException;
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

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NewsServiceAddNewsModelTest {
    @Mock()
    private AuthorService authorService;

    @Mock()
    private Repository<NewsModel> newsRepository;

    private NewsService newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(newsRepository, authorService);
    }

    @Test
    @DisplayName("When the added news contains an author who is not in the list of authors, the method will throw an AuthorNotFoundException exception")
    void create_incorrectData_throwsAuthorNotExistsException() throws CustomServiceException {
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                2L
        );
        Mockito.when(authorService.existsById(2L)).thenReturn(false);
        assertThatThrownBy(() -> newsService.create(requestDTO)).isInstanceOf(AuthorNotFoundServiceException.class);
    }
    @Test
    @DisplayName("Cases of correctness of news data. No exception should be thrown")
    void correctData_noThrownExceptions() throws CustomServiceException, CustomRepositoryException {
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "12345",
                "54321",
                1L
        );

        NewsModel addedNewsModel = new NewsModel(
                1L,
                requestDTO.getTitle(),
                requestDTO.getContent(),
                LocalDateTime.of(2024, 4, 16, 14, 37, 31),
                LocalDateTime.of(2024, 4, 16, 14, 37, 31),
                requestDTO.getAuthorId()
        );

        AuthorDTO authorDTO = new AuthorDTO(
                1L, "Author name"
        );

        Mockito.when(authorService.existsById(requestDTO.getAuthorId())).thenReturn(true);
        Mockito.when(authorService.readById(authorDTO.getId())).thenReturn(authorDTO);

        Mockito.when(newsRepository.create(Mockito.any(NewsModel.class))).thenReturn(addedNewsModel);
        Mockito.when(newsRepository.readById(addedNewsModel.getId())).thenReturn(addedNewsModel);

        LocalDateTime createdAtFrom = LocalDateTime.now();
        assertThatNoException().isThrownBy(() -> newsService.create(requestDTO));
        LocalDateTime createdAtTo= LocalDateTime.now();

        ArgumentCaptor<NewsModel> argumentCaptor = ArgumentCaptor.forClass(NewsModel.class);
        Mockito.verify(newsRepository).create(argumentCaptor.capture());

        NewsModel newsModelToSave = argumentCaptor.getValue();

        assertThat(newsModelToSave.getAuthorId()).isEqualTo(requestDTO.getAuthorId());
        assertThat(newsModelToSave.getTitle()).isEqualTo(requestDTO.getTitle());
        assertThat(newsModelToSave.getContent()).isEqualTo(requestDTO.getContent());
        assertThat(newsModelToSave.getCreateDate()).isBetween(createdAtFrom, createdAtTo);

        requestDTO.setTitle("123456789012345678901234567890");
        requestDTO.setContent(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890123456789012345"
        );

        assertThatNoException().isThrownBy(() -> newsService.create(requestDTO));
    }

    static Stream<EditNewsRequestDTO> dtoValidationDataSource() {
        return Stream.of(
                new EditNewsRequestDTO("12", "News content", 2L),
                new EditNewsRequestDTO("", "News content", 12L),
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

    @ParameterizedTest
    @MethodSource("dtoValidationDataSource")
    @DisplayName("DTOValidationException will be thrown when calling the add method.")
    void create_titleTooShort_throwsDTOValidateException(EditNewsRequestDTO request) {
        assertThatThrownBy(() -> newsService.create(request)).isInstanceOf(DTOValidationServiceException.class);
    }

    @Test
    @DisplayName("When passing an incorrect id for author, an AuthorNotFoundException will be thrown")
    void create_notFoundNewAuthor_throwsDTOValidateException() {
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                2L
        );
        assertThatThrownBy(() -> newsService.create(requestDTO)).isInstanceOf(AuthorNotFoundServiceException.class);
    }
}
