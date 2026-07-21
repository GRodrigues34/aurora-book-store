package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.aiDto.ChatRequest;
import com.github.gr.aurora_bookstore.dto.chatMessageDto.ChatMessageReadDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.service.AiService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Flux<String>> chat(@RequestBody ChatRequest request, @AuthenticationPrincipal User user) {
        Flux<String> flux = aiService.receiveUserMessage(request, user.getId());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(flux);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageReadDTO>> getChatHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(aiService.chatMessagesToDto(user.getId()));
    }
}
