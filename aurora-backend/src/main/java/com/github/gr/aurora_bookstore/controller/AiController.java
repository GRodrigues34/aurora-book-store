package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.aiDto.ChatRequest;
import com.github.gr.aurora_bookstore.dto.chatMessageDto.ChatMessageReadDto;
import com.github.gr.aurora_bookstore.service.AiService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
@RestController
public class AiController {

    private final AiService aiService;

    @PostMapping("/api/chat")
    public ResponseEntity<Flux<String>> chat(@RequestBody ChatRequest request) {
        Flux<String> flux = aiService.receiveUserMessage(request);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(flux);
    }

    @GetMapping("/api/chat/history/{userId}")
    public ResponseEntity<List<ChatMessageReadDto>> getChatHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(aiService.chatMessagesToDto(userId));
    }

}
