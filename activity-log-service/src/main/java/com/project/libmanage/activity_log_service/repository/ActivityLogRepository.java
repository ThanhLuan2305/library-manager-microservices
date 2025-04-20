package com.project.libmanage.activity_log_service.repository;

import com.project.libmanage.activity_log_service.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    Page<ActivityLog> findAll(Pageable pageable);
}
