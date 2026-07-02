package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class AiChatController {

    private final ChatClient chatClient;

    public AiChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/api/chat")
    public ResponseEntity<Flux<String>> chat(@RequestBody ChatRequest request){
        Flux<String> flux = chatClient.prompt()
                .user(request.message())
                .stream()
                .content();

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(flux);
    }

}
