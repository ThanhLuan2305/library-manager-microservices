package com.project.libmanage.library_common.client;

import com.project.libmanage.library_common.config.AuthenticaitonRequestIntercepter;
import com.project.libmanage.library_common.dto.request.UserCreateRequest;
import com.project.libmanage.library_common.dto.request.UserUpdateRequest;
import com.project.libmanage.library_common.dto.response.LoginDetailResponse;
import com.project.libmanage.library_common.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", path = "/internal", configuration = {AuthenticaitonRequestIntercepter.class})
public interface AuthFeignClient {
    @GetMapping("/auth")
    public UserResponse getAuthenticatedUser();

    @GetMapping("/login-details/{jti}")
    public LoginDetailResponse getLoginDetail(@PathVariable("jti") String jti);

    @PostMapping("/accounts")
    public void createAccount(@RequestBody UserCreateRequest userCreateRequest);

    @PutMapping("/accounts")
    public void updateAccount(@RequestParam("email") String email, @RequestBody UserUpdateRequest userUpdateRequest);

    @DeleteMapping("/accounts")
    public void deleteAccount(@RequestParam("email") String email);
}

