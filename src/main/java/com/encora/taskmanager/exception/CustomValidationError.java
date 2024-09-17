package com.encora.taskmanager.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

@Getter
@Setter
public class CustomValidationError {
    private String field;
    private String message;

    public CustomValidationError(FieldError fieldError) {
        this.field = fieldError.getField();
        this.message = fieldError.getDefaultMessage();
    }
}
