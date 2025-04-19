package com.project.libmanage.user_service.controller.user;

import com.project.libmanage.library_common.dto.response.ApiResponse;
import com.project.libmanage.library_common.dto.response.UserResponse;
import com.project.libmanage.library_common.exception.AppException;
import com.project.libmanage.user_service.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing authenticated user account operations.
 * Provides endpoints for retrieving user information and updating account details such as password,
 */
@Tag(name = "User Account Management", description = "Authenticated user account management endpoints")
@RestController
@RequestMapping("user/users")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "JWT Authentication")
public class UserController {
    private final IUserService userService;         // Service for user data operations
    private static final String RS_SUCCESS = "success"; // Constant indicating successful operation result

    /**
     * Retrieves information about the currently authenticated user.
     *
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} with the user's {@link UserResponse}
     * @throws AppException if:
     *                      - user is not authenticated
     *                      - user not found
     */
    @Operation(summary = "Get user information", description = "Returns detailed information of the authenticated user.")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .message("Get info successfully!")
                .build();
        return ResponseEntity.ok(response);
    }
}