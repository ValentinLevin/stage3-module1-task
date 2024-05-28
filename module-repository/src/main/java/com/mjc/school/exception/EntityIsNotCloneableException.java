package com.mjc.school.exception;

import com.mjc.school.model.Model;

public class EntityIsNotCloneableException extends CustomRepositoryRuntimeException {
    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Not implemented clone method in entity class (%s)";

    public EntityIsNotCloneableException(Class<? extends Model> entityClass) {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE, entityClass.getCanonicalName()));
    }
}
