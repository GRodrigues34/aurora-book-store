package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiService {
    private final ChatClient chatClient;

    public AiService(ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder.build();
    }
    public Flux<String> ProcessUserMessage(ChatRequest chatRequest){
        return chatClient.prompt().user(chatRequest.message()).stream().content();
    }
}
