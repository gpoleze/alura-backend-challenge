package com.gabrielpf.alurabackendchallange.exception;

import org.springframework.validation.FieldError;

public class DataAlreadyExistsException extends RuntimeException{
    private final FieldError fieldError;
    private static final String defaultMessage = "The given value is already present in the database";

    public DataAlreadyExistsException(Class clazz, String fieldName) {
        super(defaultMessage);
        this.fieldError = new FieldError(clazz.getName(), fieldName, defaultMessage);
    }

    public FieldError getFieldError() {
        return fieldError;
    }
}
