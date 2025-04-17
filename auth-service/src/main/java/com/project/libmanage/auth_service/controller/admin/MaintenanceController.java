package com.project.libmanage.auth_service.controller.admin;

import com.project.libmanage.auth_service.service.IMaintenanceService;
import com.project.libmanage.library_common.dto.response.ApiResponse;
import com.project.libmanage.library_common.exception.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing system maintenance mode for admin users.
 * Provides an endpoint to enable or disable maintenance mode.
 */
@RestController
@RequestMapping("admin/config")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Authentication")
@Tag(name = "Admin Maintenance Management", description = "Endpoint for managing system maintenance mode by admin users")
public class MaintenanceController {
    private final IMaintenanceService maintenanceService;

    /**
     * Sets the system maintenance mode to enabled or disabled.
     *
     * @param status the maintenance mode status (true to enable, false to disable)
     * @return a {@link ResponseEntity} containing:
     * - an {@link ApiResponse} with a success message
     * @throws AppException if:
     *                      - user not authenticated (ErrorCode.UNAUTHENTICATED)
     *                      - user not authorized (ErrorCode.UNAUTHORIZED)
     * @implNote Delegates to {@link IMaintenanceService} to set maintenance mode and returns a success message in an {@link ApiResponse}.
     */
    @PostMapping("/maintenance/{status}")
    @Operation(summary = "Set maintenance mode",
            description = "Sets the system maintenance mode to enabled or disabled.")
    public ResponseEntity<ApiResponse<String>> setMaintenanceMode(@PathVariable("status") boolean status) {
        maintenanceService.setMaintenanceMode(status);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .message("Server in Maintenance: " + status)
                .result("success")
                .build();
        return ResponseEntity.ok(response);
    }
}