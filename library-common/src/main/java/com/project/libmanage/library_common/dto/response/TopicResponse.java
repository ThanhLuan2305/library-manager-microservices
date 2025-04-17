package com.project.libmanage.library_common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicResponse {
    private String name;
    private String createdBy;
    private String description;
}
