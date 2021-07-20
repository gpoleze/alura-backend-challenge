package com.gabrielpf.alurabackendchallange.vo.in;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VideosVoInTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void cannotInstantiateWithEmptyTitle() {
        // I'd name the test to something like
        // invalidEmailShouldFailValidation()

        VideosVoIn videosVoIn = new VideosVoIn("description", "", "url");
        Set<ConstraintViolation<VideosVoIn>> violations = validator.validate(videosVoIn);
        assertFalse(violations.isEmpty());
    }

}