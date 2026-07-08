package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.aiDto.ChatRequest;
import com.github.gr.aurora_bookstore.model.entity.ChatMessage;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.repository.ChatMessageRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiService {
    private final ChatClient chatClient;
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;

    public AiService(ChatClient.Builder chatClientBuilder,
            ChatMessageRepository chatMessageRepository,
            UserService userService) {
        this.chatClient = chatClientBuilder.build();
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
    }

    public Flux<String> processUserMessage(String message) {
        return chatClient.prompt().user(message).stream().content();
    }

    public void saveChatMessage(String message, Long userId, String chatRole) {
        User user = userService.getEntityById(userId);
        ChatMessage newChatMessage = ChatMessage.builder()
                .user(user)
                .chatRole(chatRole)
                .content(message)
                .build();
        chatMessageRepository.save(newChatMessage);
    }

    public Flux<String> receiveUserMessage(ChatRequest chatRequest) {
        String userMessage = chatRequest.message();
        Long userId = Long.valueOf(chatRequest.userId());

        saveChatMessage(userMessage, userId, "user");

        StringBuilder fullAiResponse = new StringBuilder();

        return processUserMessage(userMessage)
                .doOnNext(chunk -> fullAiResponse.append(chunk))
                .doOnComplete(() -> {
                    saveChatMessage(fullAiResponse.toString(), userId, "ai");
                });
    }
}
