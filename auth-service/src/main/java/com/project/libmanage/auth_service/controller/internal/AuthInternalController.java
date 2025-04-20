package com.project.libmanage.auth_service.controller.internal;

import com.project.libmanage.auth_service.service.IAuthenticationService;
import com.project.libmanage.library_common.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/auth")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "JWT Authentication")
public class AuthInternalController {
    private final IAuthenticationService authenticationService;
    @GetMapping
    public UserResponse getAuthenticatedUser() {
        return authenticationService.getAuthenticatedUser();
    }
}
