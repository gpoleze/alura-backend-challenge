package com.gabrielpf.alurabackendchallange.controller.form;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Predicate;

public interface UpdateForm<T> {

    T update(T entity);

    default boolean hasAllFieldsBlank() {
        Predicate<Field> hasValue = f -> {
            f.setAccessible(true);
            try {
                return f.get(this) != null && !((String) f.get(this)).isBlank();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return true;
            }
        };

        return Arrays.stream(this.getClass().getDeclaredFields()).noneMatch(hasValue);
    }
}
