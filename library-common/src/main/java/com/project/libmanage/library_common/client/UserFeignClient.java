package com.project.libmanage.library_common.client;

import com.project.libmanage.library_common.config.AuthenticaitonRequestIntercepter;
import com.project.libmanage.library_common.dto.request.ChangeMailRequest;
import com.project.libmanage.library_common.dto.request.ChangePasswordRequest;
import com.project.libmanage.library_common.dto.request.ChangePhoneRequest;
import com.project.libmanage.library_common.dto.request.UserCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", path = "/internal/users", configuration = {AuthenticaitonRequestIntercepter.class})
public interface UserFeignClient {
    @GetMapping("/banned-borrowing")
    public boolean isBannedFromBorrowing(@RequestParam("email") String email);

    @PutMapping("/late-return")
    public void updateLateReturn(@RequestParam("email") String email);

    @PutMapping("/update-email")
    public void updateEmail(@RequestBody ChangeMailRequest changeMailRequest);

    @PutMapping("/update-password")
    public void updatePassword(@RequestBody ChangePasswordRequest changePasswordRequest);

    @PutMapping("/update-phone")
    public void updatePhone(@RequestBody ChangePhoneRequest changePhoneRequest);

    @PostMapping
    public void createUserInternal(@RequestBody UserCreateRequest userCreateRequest);
}