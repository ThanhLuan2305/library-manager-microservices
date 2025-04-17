package com.project.libmanage.auth_service.service;


import com.project.libmanage.library_common.dto.request.*;
import com.project.libmanage.library_common.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface IAccountService {
    UserResponse registerUser(RegisterRequest registerRequest);
    boolean verifyEmail(String otp, String email);
    boolean verifyPhone(String otp, String phone);
    void verifyChangeEmail(VerifyChangeMailRequest request);
    void changeEmail(ChangeMailRequest request);
    void verifyChangePhone(VerifyChangePhoneRequest request);
    void changePhone(ChangePhoneRequest request);
    List<String> getRolesUser(String token, HttpServletResponse response);
}
