package com.mjc.school.mapper;

import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.model.Author;
import org.modelmapper.ModelMapper;

public class AuthorMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    private AuthorMapper() {}

    public static AuthorDTO toAuthorDTO(Author author) {
        return author == null ? null : modelMapper.map(author, AuthorDTO.class);
    }
}
