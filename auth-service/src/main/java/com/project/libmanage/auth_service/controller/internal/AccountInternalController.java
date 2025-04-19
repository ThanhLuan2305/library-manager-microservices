package com.project.libmanage.auth_service.controller.internal;

import com.project.libmanage.auth_service.service.IAccountService;
import com.project.libmanage.library_common.dto.request.UserCreateRequest;
import com.project.libmanage.library_common.dto.request.UserUpdateRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("internal/accounts")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "JWT Authentication")
public class AccountInternalController {
    private final IAccountService accountService;
    @PostMapping
    public void createAccount(@RequestBody UserCreateRequest userCreateRequest) {
        accountService.createAccount(userCreateRequest);
    }
    @PutMapping
    public void updateAccount(@RequestParam("email") String email, @RequestBody UserUpdateRequest userUpdateRequest) {
        accountService.updateAccount(email, userUpdateRequest);
    }
    @DeleteMapping
    public void deleteAccount(@RequestParam("email") String email) {
        accountService.deleteAccount(email);
    }
}
