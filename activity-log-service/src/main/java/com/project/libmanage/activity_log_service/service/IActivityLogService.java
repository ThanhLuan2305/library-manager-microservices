package com.project.libmanage.activity_log_service.service;

import com.project.libmanage.activity_log_service.entity.ActivityLog;
import com.project.libmanage.library_common.dto.request.LogActionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IActivityLogService {
    void logAction(LogActionRequest logActionRequest);
    Page<ActivityLog> getActivityLogs(Pageable pageable);
    void deleteAllLogs();
    ActivityLog getActivityLog(String id);
}
