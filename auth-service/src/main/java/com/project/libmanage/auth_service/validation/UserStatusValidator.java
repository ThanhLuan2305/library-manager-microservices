package com.project.libmanage.auth_service.validation;

import com.project.libmanage.auth_service.entity.User;
import com.project.libmanage.library_common.constant.ErrorCode;
import com.project.libmanage.library_common.constant.VerificationStatus;
import com.project.libmanage.library_common.exception.AppException;
import org.springframework.stereotype.Component;

@Component
public class UserStatusValidator {
    public boolean validate(User user) {
        // check status user
        if (user.isDeleted()) {
            throw new AppException(ErrorCode.USER_IS_DELETED);
        }
        // check status verify user
        if (user.getVerificationStatus() != VerificationStatus.FULLY_VERIFIED) {
            throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        }
        return true;
    }
}
