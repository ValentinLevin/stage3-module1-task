package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.News;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.EditNewsRequestDTO;
import com.mjc.school.service.dto.NewsDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NewsMapperTest {

    @Test
    @DisplayName("Checking the correctness of entity to DTO conversion")
    void checkEntityToDTO() {
        News news =
                new News(
                        1L,
                        "News 1 title",
                        "New 1 content",
                        LocalDateTime.of(2024, 4, 14, 20, 43, 1),
                        LocalDateTime.of(2024, 5, 13, 21, 3, 31),
                        2L
                );

        NewsDTO expectedDTO =
                new NewsDTO(
                        1L,
                        "News 1 title",
                        "New 1 content",
                        "2024-04-14T20:43:01",
                        "2024-05-13T21:03:31",
                        new AuthorDTO(2L, null)
                );

        NewsDTO actualDTO = NewsMapper.toNewsDTO(news);

        assertThat(actualDTO).isEqualTo(expectedDTO);
    }

    @Test
    @DisplayName("Checking the correctness of entity with null lastUpdateDate to DTO conversion")
    void checkEntityToDTO_withNullLastUpdateDate() {
        News news =
                new News(
                        1L,
                        "News 1 title",
                        "New 1 content",
                        LocalDateTime.of(2024, 4, 14, 20, 43, 1),
                        null,
                        2L
                );

        NewsDTO expectedDTO =
                new NewsDTO(
                        1L,
                        "News 1 title",
                        "New 1 content",
                        "2024-04-14T20:43:01",
                        null,
                        new AuthorDTO(2L, null)
                );

        NewsDTO actualDTO = NewsMapper.toNewsDTO(news);

        assertThat(actualDTO).isEqualTo(expectedDTO);
    }

    @Test
    @DisplayName("Checking dto to entity conversion")
    void checkDTOToEntity() {
        EditNewsRequestDTO newsDTO = new EditNewsRequestDTO(
                "News 1 title",
                "News 1 content",
                1L
        );

        News actualNews = NewsMapper.fromEditNewsRequestDTO(newsDTO);

        assertThat(actualNews)
                .extracting("title", "content", "authorId")
                .containsOnly(newsDTO.getTitle(), newsDTO.getContent(), newsDTO.getAuthorId());
    }
}
