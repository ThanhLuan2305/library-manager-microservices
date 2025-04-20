package com.project.libmanage.activity_log_service.service.impl;

import com.project.libmanage.activity_log_service.entity.ActivityLog;
import com.project.libmanage.activity_log_service.repository.ActivityLogRepository;
import com.project.libmanage.activity_log_service.service.IActivityLogService;
import com.project.libmanage.library_common.constant.ErrorCode;
import com.project.libmanage.library_common.constant.UserAction;
import com.project.libmanage.library_common.dto.request.LogActionRequest;
import com.project.libmanage.library_common.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Implementation of {@link IActivityLogService} for managing activity logging operations.
 * Provides methods to log user actions, retrieve logs, and delete logs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogServiceServiceImpl implements IActivityLogService {
    private final ActivityLogRepository activityLogRepository; // Repository for persisting and querying activity logs

    /**
     * Logs a user action with relevant details and optional before/after states.
     *
     * @param logActionRequest  the request containing action details
     * @implNote Creates and saves an {@link ActivityLog} entity, then logs the saved entity for verification.
     */
    @Override
    public void logAction(LogActionRequest logActionRequest) {
        // Build log entity; captures action details with current timestamp
        ActivityLog logActivity = ActivityLog.builder()
                .userId(logActionRequest.getUserId())          // Links log to user
                .email(logActionRequest.getEmail())            // Provides user identifier
                .action(logActionRequest.getAction())          // Specifies action type
                .details(logActionRequest.getDetails())        // Describes action context
                .timestamp(Instant.now()) // Records exact time of action
                .beforeChange(logActionRequest.getBeforeChange()) // Optional: state before action
                .afterChange(logActionRequest.getAfterChange())   // Optional: state after action
                .build();

        // Persist log; assumes auto-generated ID
        ActivityLog newLog = activityLogRepository.save(logActivity);
        // Log saved entity; aids debugging and verification
        log.info("Check activity log: {}", newLog);
    }

    /**
     * Retrieves a paginated list of all activity logs.
     *
     * @param pageable the {@link Pageable} object with pagination details (e.g., page number, size)
     * @return a {@link Page} of {@link ActivityLog} entities
     * @implNote Fetches logs from the repository with pagination support.
     */
    @Override
    public Page<ActivityLog> getActivityLogs(Pageable pageable) {
        // Fetch all logs with pagination; assumes repository handles sorting/filtering if specified
        return activityLogRepository.findAll(pageable);
    }

    /**
     * Deletes all activity logs from the system.
     *
     * @implNote Performs a bulk delete operation on the log repository.
     */
    @Override
    public void deleteAllLogs() {
        // Delete all logs; assumes no additional validation required
        activityLogRepository.deleteAll();
    }

    /**
     * Retrieves a specific activity log by its ID.
     *
     * @param id the ID of the activity log to retrieve
     * @return the {@link ActivityLog} entity corresponding to the ID
     * @throws AppException if the log is not found (ErrorCode.ACTIVITY_LOG_NOT_EXISTED)
     * @implNote Fetches a single log from the repository, throwing an exception if not found.
     */
    @Override
    public ActivityLog getActivityLog(String id) {
        // Fetch log by ID; uses Optional to handle absence, throws if not found
        return activityLogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ACTIVITY_LOG_NOT_EXISTED));
    }
}