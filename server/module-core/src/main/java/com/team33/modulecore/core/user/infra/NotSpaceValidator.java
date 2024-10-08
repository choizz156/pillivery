package com.team33.modulecore.core.user.infra;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotSpaceValidator implements ConstraintValidator<NotSpace, String> {

    @Override
    public void initialize(NotSpace constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return hasWhitespace(value);
    }

    public boolean hasWhitespace(String str) {
        for(char c :str.toCharArray()){
            if(Character.isWhitespace(c)){
                return false;
            }
        }
        return true;
    }
}
