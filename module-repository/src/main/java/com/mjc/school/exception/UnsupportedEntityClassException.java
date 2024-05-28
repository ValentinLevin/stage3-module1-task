package com.mjc.school.exception;

import com.mjc.school.model.Model;

public class UnsupportedEntityClassException extends CustomRepositoryRuntimeException {
    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Unsupported model class %s";

    public UnsupportedEntityClassException(Class<? extends Model> entityClass) {
        super(String.format(EXCEPTION_MESSAGE_TEMPLATE, entityClass.getCanonicalName()));
    }
}
