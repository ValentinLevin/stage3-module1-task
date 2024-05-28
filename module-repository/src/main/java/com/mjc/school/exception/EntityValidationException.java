package com.mjc.school.exception;

public class EntityValidationException extends CustomRepositoryException {
    private static final String MESSAGE_TEMPLATE = "The following errors were detected during the check: %s";

    public EntityValidationException(String constrainViolation) {
        super(String.format(MESSAGE_TEMPLATE, constrainViolation));
    }
}
