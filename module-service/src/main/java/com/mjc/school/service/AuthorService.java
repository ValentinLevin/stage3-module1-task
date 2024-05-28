package com.mjc.school.service;

import com.mjc.school.dto.AuthorDTO;
import com.mjc.school.exception.AuthorNotFoundServiceException;
import com.mjc.school.exception.NullAuthorIdServiceException;

import java.util.Map;

public interface AuthorService {
    boolean existsById(Long id) throws AuthorNotFoundServiceException;
    AuthorDTO readById(Long id) throws NullAuthorIdServiceException, AuthorNotFoundServiceException;
    Map<Long, AuthorDTO> readMap();
}
