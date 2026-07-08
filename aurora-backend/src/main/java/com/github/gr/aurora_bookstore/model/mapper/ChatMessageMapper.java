package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.chatMessageDto.ChatMessageReadDto;
import com.github.gr.aurora_bookstore.model.entity.ChatMessage;

public class ChatMessageMapper {

    public static ChatMessageReadDto toReadDto(ChatMessage chatMessage) {
        if (chatMessage == null) return null;
        ChatMessageReadDto dto = new ChatMessageReadDto();
        dto.setId(chatMessage.getId());
        dto.setChatRole(chatMessage.getChatRole());
        dto.setContent(chatMessage.getContent());
        dto.setCreatedAt(chatMessage.getCreatedAt());
        return dto;
    }
}
