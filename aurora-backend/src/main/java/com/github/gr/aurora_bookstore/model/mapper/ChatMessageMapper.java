package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.chatMessageDto.ChatMessageReadDTO;
import com.github.gr.aurora_bookstore.model.entity.ChatMessage;

public class ChatMessageMapper {

    public static ChatMessageReadDTO toReadDto(ChatMessage chatMessage) {
        if (chatMessage == null) return null;
        ChatMessageReadDTO dto = new ChatMessageReadDTO();
        dto.setId(chatMessage.getId());
        dto.setChatRole(chatMessage.getChatRole());
        dto.setContent(chatMessage.getContent());
        dto.setCreatedAt(chatMessage.getCreatedAt());
        return dto;
    }
}
