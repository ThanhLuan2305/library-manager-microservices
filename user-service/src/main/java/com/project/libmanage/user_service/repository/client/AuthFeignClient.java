package com.project.libmanage.user_service.repository.client;

import com.project.libmanage.library_common.dto.request.UserCreateRequest;
import com.project.libmanage.library_common.dto.request.UserUpdateRequest;
import com.project.libmanage.library_common.dto.response.LoginDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", path = "/internal")
public interface AuthFeignClient {
    @GetMapping("/login-details/{jti}")
    LoginDetailResponse getLoginDetail(@PathVariable("jti") String jti);

    @PostMapping("/accounts")
    public void createAccount(@RequestBody UserCreateRequest userCreateRequest);

    @PutMapping("/accounts")
    public void updateAccount(@RequestParam("email") String email, @RequestBody UserUpdateRequest userUpdateRequest);

    @DeleteMapping("/accounts")
    public void deleteAccount(@RequestParam("email") String email);
}
