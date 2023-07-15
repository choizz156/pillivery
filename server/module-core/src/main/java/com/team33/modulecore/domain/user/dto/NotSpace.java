package com.team33.modulecore.domain.user.dto;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotSpaceValidator.class})
public @interface NotSpace {
    String message() default "글자에 공백이 있습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
