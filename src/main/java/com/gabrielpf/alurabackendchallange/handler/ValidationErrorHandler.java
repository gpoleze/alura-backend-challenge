package com.gabrielpf.alurabackendchallange.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gabrielpf.alurabackendchallange.dto.FormErrorDto;
import com.gabrielpf.alurabackendchallange.exception.DataAlreadyExistsException;
import com.gabrielpf.alurabackendchallange.exception.EntityCannotBeDeletedException;

@RestControllerAdvice
public class ValidationErrorHandler {

    private final MessageSource messageSource;

    @Autowired
    public ValidationErrorHandler(MessageSource messageSource) {this.messageSource = messageSource;}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public List<FormErrorDto> handle(MethodArgumentNotValidException exception) {

        final List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        return fieldErrors
                .stream()
                .map(fieldError -> {
                    String mensage = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
                    return new FormErrorDto(fieldError.getField(), mensage);
                })
                .collect(Collectors.toList());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({DataAlreadyExistsException.class})
    public FormErrorDto handleDataAlreadyExists(DataAlreadyExistsException exception) {
        return new FormErrorDto(exception.getFieldError().getField(), exception.getMessage());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EntityCannotBeDeletedException.class})
    public FormErrorDto handle(EntityCannotBeDeletedException exception) {
        return new FormErrorDto(exception.getFieldError().getField(), exception.getMessage());
    }
}