package com.project.libmanage.activity_log_service.controller;

import com.project.libmanage.activity_log_service.service.IActivityLogService;
import com.project.libmanage.library_common.dto.request.LogActionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/activity-log")
@RequiredArgsConstructor
public class ActivityLogInternalController {
    private final IActivityLogService activityLogService;
    @PostMapping
    public void logAction(@RequestBody LogActionRequest logActionRequest) {
        activityLogService.logAction(logActionRequest);
    }
}
