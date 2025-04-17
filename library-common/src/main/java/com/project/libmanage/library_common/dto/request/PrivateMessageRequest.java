package com.project.libmanage.library_common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request data for sending a private message between users")
public class PrivateMessageRequest {

    @Schema(description = "ID of the user sending the message", example = "1")
    private Long senderId;

    @Schema(description = "ID of the user receiving the message", example = "2")
    private Long receiverId;

    @Schema(description = "Content of the message to be sent", example = "Hi, how are you today?")
    private String content;
}