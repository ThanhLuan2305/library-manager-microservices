package com.project.libmanage.library_common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Details of a private message exchanged between users")
public class PrivateMessageResponse {

    @Schema(description = "Unique identifier of the message", example = "msg123")
    private String id;

    @Schema(description = "ID of the user who sent the message", example = "1")
    private Long senderId;

    @Schema(description = "ID of the user who received the message", example = "2")
    private Long receiverId;

    @Schema(description = "Content of the message", example = "Hello, how are you?")
    private String content;

    @Schema(description = "Timestamp when the message was sent", example = "2023-06-01T12:00:00Z")
    private Instant timestamp;

    private boolean delivered;

    private boolean read;
}