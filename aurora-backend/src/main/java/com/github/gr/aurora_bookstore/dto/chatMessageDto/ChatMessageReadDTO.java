package com.github.gr.aurora_bookstore.dto.chatMessageDto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageReadDTO {
    private Long id;
    private String chatRole;
    private String content;
    private LocalDateTime createdAt;
}
