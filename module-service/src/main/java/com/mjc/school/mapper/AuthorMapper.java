package com.mjc.school.mapper;

import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.model.AuthorModel;
import org.modelmapper.ModelMapper;

public class AuthorMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    private AuthorMapper() {}

    public static AuthorDTO toAuthorDTO(AuthorModel authorModel) {
        return authorModel == null ? null : modelMapper.map(authorModel, AuthorDTO.class);
    }
}
