package com.project.libmanage.activity_log_service.entity;

import com.project.libmanage.library_common.constant.UserAction;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "activity_logs")
@Builder
@Data
public class ActivityLog {
    @Id
    private String id;
    private Long userId;
    private String email;
    private UserAction action;
    private String details;
    private Instant timestamp;
    private Object beforeChange;
    private Object afterChange;
}
