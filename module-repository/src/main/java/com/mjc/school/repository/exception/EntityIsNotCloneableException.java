package com.mjc.school.repository.exception;

import com.mjc.school.repository.model.Entity;

public class EntityIsNotCloneableException extends CustomRepositoryRuntimeException {
    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Not implemented clone method in entity class (%s)";

    public EntityIsNotCloneableException(Class<? extends Entity> entityClass) {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE, entityClass.getCanonicalName()));
    }
}
