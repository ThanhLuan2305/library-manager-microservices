package com.project.libmanage.auth_service.service;

import com.project.libmanage.library_common.dto.request.AuthenticationRequest;
import com.project.libmanage.library_common.dto.response.AuthenticationResponse;
import com.project.libmanage.library_common.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response);

    AuthenticationResponse refreshToken(String refreshToken, HttpServletResponse response);

    void logout(String accessToken, HttpServletResponse response);
    UserResponse getAuthenticatedUser();
}
