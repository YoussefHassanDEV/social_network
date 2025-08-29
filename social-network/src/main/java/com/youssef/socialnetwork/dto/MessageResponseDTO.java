package com.youssef.socialnetwork.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponseDTO {
    private Long id;
    private Long senderId;      // ✅ must exist
    private String senderName;  // ✅ must exist
    private Long receiverId;    // ✅ must exist
    private String receiverName;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
}
