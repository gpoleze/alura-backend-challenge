package com.gabrielpf.alurabackendchallange.exception;

public class EntityDoesNotExistException extends RuntimeException {
    private static final String defaultMessage = "Item Not Found";

    public EntityDoesNotExistException() {
        super(defaultMessage);
    }

    public EntityDoesNotExistException(String message) {
        super(message);
    }
}
