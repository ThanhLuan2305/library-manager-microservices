package com.project.libmanage.user_service.controller.internal;

import com.project.libmanage.library_common.dto.request.ChangeMailRequest;
import com.project.libmanage.library_common.dto.request.ChangePasswordRequest;
import com.project.libmanage.library_common.dto.request.ChangePhoneRequest;
import com.project.libmanage.library_common.dto.request.UserCreateRequest;
import com.project.libmanage.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("internal/users")
public class UserInteralController {
    private final IUserService userService;
    @GetMapping("/banned-borrowing")
    public boolean isBannedFromBorrowing(@RequestParam("email") String email) {
        return userService.isBannedFromBorrowing(email);
    }
    @PutMapping("/late-return")
    public void updateLateReturn(@RequestParam("email") String email) {
        userService.updateLateReturn(email);
    }

    @PutMapping("/update-email")
    public void updateEmail(@RequestBody ChangeMailRequest changeMailRequest) {
        userService.updateEmail(changeMailRequest);
    }

    @PutMapping("/update-password")
    public void updatePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.updatePassword(changePasswordRequest);
    }

    @PutMapping("/update-phone")
    public void updatePhone(@RequestBody ChangePhoneRequest changePhoneRequest) {
        userService.updatePhone(changePhoneRequest);
    }

    @PostMapping
    public void createUserInternal(@RequestBody UserCreateRequest userCreateRequest) {
        userService.createUserInternal(userCreateRequest);
    }
}
