package com.mjc.school.service.mapper;

import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.repository.model.Author;
import org.modelmapper.ModelMapper;

public class AuthorMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    private AuthorMapper() {}

    public static AuthorDTO toAuthorDTO(Author author) {
        return author == null ? null : modelMapper.map(author, AuthorDTO.class);
    }
}
