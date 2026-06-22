package com.uce.Tutomatch.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InstitutionalEmailValidator implements ConstraintValidator<InstitutionalEmail, String> {

    private static final String INSTITUTIONAL_DOMAIN = "@uce.edu.ec";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        String email = value.trim().toLowerCase();
        return email.endsWith(INSTITUTIONAL_DOMAIN);
    }
}
