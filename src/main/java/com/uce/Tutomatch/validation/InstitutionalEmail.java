package com.uce.Tutomatch.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = InstitutionalEmailValidator.class)
@Documented
public @interface InstitutionalEmail {

    String message() default "El correo debe pertenecer al dominio institucional @uce.edu.ec";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
