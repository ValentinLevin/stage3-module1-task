package com.mjc.school.repository.exception;

import com.mjc.school.repository.model.Entity;

public class UnsupportedEntityClassException extends CustomRepositoryRuntimeException {
    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Unsupported model class %s";

    public UnsupportedEntityClassException(Class<? extends Entity> entityClass) {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE, entityClass.getCanonicalName()));
    }
}
