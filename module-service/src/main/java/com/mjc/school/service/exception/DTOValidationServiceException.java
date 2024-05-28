package com.mjc.school.service.exception;

public class DTOValidationServiceException extends CustomServiceException {
    private static final String MESSAGE_TEMPLATE = "The following errors were detected during the check: %s";

    public DTOValidationServiceException(String constrainViolations) {
        super(String.format(MESSAGE_TEMPLATE, constrainViolations));
    }
}
