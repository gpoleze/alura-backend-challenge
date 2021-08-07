package com.gabrielpf.alurabackendchallange.dto;

public class FormErrorDto {

    private final String field;
    private final String error;

    public FormErrorDto(String field, String error) {
        this.field = field;
        this.error = error;
    }

    public String getField() {
        return field;
    }

    public String getError() {
        return error;
    }
}
