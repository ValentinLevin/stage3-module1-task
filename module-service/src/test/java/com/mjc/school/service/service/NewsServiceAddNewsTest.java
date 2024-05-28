package com.mjc.school.service.service;

import com.mjc.school.repository.exception.CustomRepositoryException;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.repository.Repository;
import com.mjc.school.service.dto.EditNewsRequestDTO;
import com.mjc.school.service.exception.AuthorNotFoundServiceException;
import com.mjc.school.service.exception.DTOValidationServiceException;
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
class NewsServiceAddNewsTest {
    @Mock()
    private Repository<Author> authorRepository;

    @Mock()
    private Repository<News> newsRepository;

    private NewsService newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(newsRepository, authorRepository);
    }

    @Test
    @DisplayName("When the added news contains an author who is not in the list of authors, the method will throw an AuthorNotFoundException exception")
    void add_incorrectData_throwsAuthorNotExistsException() throws CustomRepositoryException {
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                2L
        );
        Mockito.when(authorRepository.existsById(2L)).thenReturn(false);
        assertThatThrownBy(() -> newsService.add(requestDTO)).isInstanceOf(AuthorNotFoundServiceException.class);
    }
    @Test
    @DisplayName("Cases of correctness of news data. No exception should be thrown")
    void correctData_noThrownExceptions() throws CustomRepositoryException {
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "12345",
                "54321",
                1L
        );

        News addedNews = new News(
                1L,
                requestDTO.getTitle(),
                requestDTO.getContent(),
                LocalDateTime.of(2024, 4, 16, 14, 37, 31),
                LocalDateTime.of(2024, 4, 16, 14, 37, 31),
                requestDTO.getAuthorId()
        );

        Author author = new Author(
                1L, "Author name"
        );

        Mockito.when(authorRepository.existsById(requestDTO.getAuthorId())).thenReturn(true);
        Mockito.when(authorRepository.findById(author.getId())).thenReturn(author);

        Mockito.when(newsRepository.save(Mockito.any(News.class))).thenReturn(addedNews);
        Mockito.when(newsRepository.findById(addedNews.getId())).thenReturn(addedNews);

        LocalDateTime createdAtFrom = LocalDateTime.now();
        assertThatNoException().isThrownBy(() -> newsService.add(requestDTO));
        LocalDateTime createdAtTo= LocalDateTime.now();

        ArgumentCaptor<News> argumentCaptor = ArgumentCaptor.forClass(News.class);
        Mockito.verify(newsRepository).save(argumentCaptor.capture());

        News newsToSave = argumentCaptor.getValue();

        assertThat(newsToSave.getAuthorId()).isEqualTo(requestDTO.getAuthorId());
        assertThat(newsToSave.getTitle()).isEqualTo(requestDTO.getTitle());
        assertThat(newsToSave.getContent()).isEqualTo(requestDTO.getContent());
        assertThat(newsToSave.getCreateDate()).isBetween(createdAtFrom, createdAtTo);

        requestDTO.setTitle("123456789012345678901234567890");
        requestDTO.setContent(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890123456789012345"
        );

        assertThatNoException().isThrownBy(() -> newsService.add(requestDTO));
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
    void add_titleTooShort_throwsDTOValidateException(EditNewsRequestDTO request) {
        assertThatThrownBy(() -> newsService.add(request)).isInstanceOf(DTOValidationServiceException.class);
    }

    @Test
    @DisplayName("When passing an incorrect id for author, an AuthorNotFoundException will be thrown")
    void add_notFoundNewAuthor_throwsDTOValidateException() {
        EditNewsRequestDTO requestDTO = new EditNewsRequestDTO(
                "News title",
                "News content",
                2L
        );
        assertThatThrownBy(() -> newsService.add(requestDTO)).isInstanceOf(AuthorNotFoundServiceException.class);
    }
}
