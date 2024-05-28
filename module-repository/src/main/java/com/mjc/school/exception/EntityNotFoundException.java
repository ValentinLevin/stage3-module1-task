package com.mjc.school.exception;

import com.mjc.school.model.Model;

public class EntityNotFoundException extends CustomRepositoryException {
    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Not found entity with class %s by id %d";

    public EntityNotFoundException(Long entityId, Class<? extends Model> entityClass) {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE, entityClass.getCanonicalName(), entityId));
    }
}
