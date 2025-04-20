package com.project.libmanage.library_common.dto.request;

import com.project.libmanage.library_common.constant.UserAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class LogActionRequest {
    private Long userId;
    private String email;
    private UserAction action;
    private String details;
    private Object beforeChange;
    private Object afterChange;
}
