package com.gabrielpf.alurabackendchallange.exception;

import org.springframework.validation.FieldError;

public class EntityCannotBeDeletedException extends RuntimeException {
    private static final String defaultMessage = "The item is related to other tables";
    private final FieldError fieldError;

    public EntityCannotBeDeletedException(Class clazz, String fieldName) {
        super(defaultMessage);
        this.fieldError = new FieldError(clazz.getName(), fieldName, defaultMessage);
    }

    public EntityCannotBeDeletedException(Class clazz, String fieldName, String errorMessage) {
        super(errorMessage);
        this.fieldError = new FieldError(clazz.getName(), fieldName, errorMessage);
    }

    public FieldError getFieldError() {
        return fieldError;
    }
}
