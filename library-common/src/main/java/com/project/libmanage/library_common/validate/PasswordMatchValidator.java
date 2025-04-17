package com.project.libmanage.library_common.validate;

import com.project.libmanage.library_common.dto.request.ChangePasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, ChangePasswordRequest> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ChangePasswordRequest value, ConstraintValidatorContext context) {
        if (value.getNewPassword() == null || value.getConfirmPassword() == null) {
            return false;
        }
        return value.getNewPassword().equals(value.getConfirmPassword());
    }
}
