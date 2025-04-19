package com.project.libmanage.auth_service.controller.internal;

import com.project.libmanage.auth_service.service.ILoginDetailService;
import com.project.libmanage.library_common.dto.response.LoginDetailResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/login-details")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "JWT Authentication")
public class LoginDetailInternalController {
    private final ILoginDetailService loginDetailService;
    @GetMapping({"/{jti}"})
    public LoginDetailResponse getLoginDetail(@PathVariable("jti") String jti) {
        return loginDetailService.getLoginDetailByJti(jti);
    }
}
