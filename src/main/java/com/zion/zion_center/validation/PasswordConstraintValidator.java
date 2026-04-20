package com.zion.zion_center.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Let @NotBlank handle null/blank separately
        if (password == null || password.isBlank()) return true;

        List<String> failures = new ArrayList<>();

        if (password.length() < 8)
            failures.add("at least 8 characters");

        if (password.length() > 128)
            failures.add("at most 128 characters");

        if (!password.matches(".*[A-Z].*"))
            failures.add("at least one uppercase letter");

        if (!password.matches(".*[a-z].*"))
            failures.add("at least one lowercase letter");

        if (!password.matches(".*\\d.*"))
            failures.add("at least one digit (0-9)");

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"))
            failures.add("at least one special character (!@#$%^&* etc.)");

        if (failures.isEmpty()) return true;

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "Password must contain: " + String.join(", ", failures)
        ).addConstraintViolation();

        return false;
    }
}
