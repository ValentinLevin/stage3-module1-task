package com.mjc.school.mapper;

import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.model.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorMapperTest {

    @Test
    @DisplayName("Checking the correctness of entity to DTO conversion")
    void checkEntityToDTO() {
        Author author = new Author(1L, "Author 1 name");
        AuthorDTO exceptedDTO = new AuthorDTO(1L, "Author 1 name");

        AuthorDTO actualDTO = AuthorMapper.toAuthorDTO(author);

        assertThat(actualDTO).isEqualTo(exceptedDTO);
    }
}
