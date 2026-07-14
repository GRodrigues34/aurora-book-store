package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.aiDto.ChatRequest;
import com.github.gr.aurora_bookstore.dto.chatMessageDto.ChatMessageReadDTO;
import com.github.gr.aurora_bookstore.service.AiService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/chat")
public class AiController {

    private final AiService aiService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Flux<String>> chat(@RequestBody ChatRequest request) {
        Flux<String> flux = aiService.receiveUserMessage(request);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(flux);
    }


    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageReadDTO>> getChatHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(aiService.chatMessagesToDto(userId));
    }

}
