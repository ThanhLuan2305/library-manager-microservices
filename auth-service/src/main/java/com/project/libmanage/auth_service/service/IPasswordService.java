package com.project.libmanage.auth_service.service;


import com.project.libmanage.library_common.dto.request.ChangePasswordRequest;
import com.project.libmanage.library_common.dto.request.ResetPasswordRequest;

public interface IPasswordService {
    boolean changePassword(ChangePasswordRequest request);

    void forgetPassword(String email);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
