package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.aiDto.ChatRequest;
import com.github.gr.aurora_bookstore.dto.chatMessageDto.ChatMessageReadDto;
import com.github.gr.aurora_bookstore.model.entity.ChatMessage;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.mapper.ChatMessageMapper;
import com.github.gr.aurora_bookstore.repository.ChatMessageRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiService {

    @Value("classpath:system-prompt.txt")
    Resource systemPrompt;

    @Value("classpath:policies.md")
    Resource policies;

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

    public Flux<String> processUserMessage(List<Message> messages) {
        try {
            String systemText = systemPrompt.getContentAsString(StandardCharsets.UTF_8);

            return chatClient.prompt()
                    .system(systemText)
                    .messages(messages)
                    .stream()
                    .content();
        } catch (IOException e) {
            throw new RuntimeException("Error reading AI system prompt resource", e);
        }
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

    public List<Message> getSpringAiHistory(Long userId) {
        List<ChatMessage> lastMessages = chatMessageRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId).reversed();
        List<Message> springAiMessages = new ArrayList<>();

        for (ChatMessage chatMessage : lastMessages) {
            if ("user".equalsIgnoreCase(chatMessage.getChatRole())) {
                springAiMessages.add(new UserMessage(chatMessage.getContent()));
            } else {
                springAiMessages.add(new AssistantMessage(chatMessage.getContent()));
            }
        }
        return springAiMessages;
    }

    public List<ChatMessageReadDto> chatMessagesToDto(Long userId) {
        List<ChatMessage> lastMessages = chatMessageRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId).reversed();
        List<ChatMessageReadDto> chatMessageReadDtos = new ArrayList<>();
        for (ChatMessage chatMessage : lastMessages) {
            chatMessageReadDtos.add(ChatMessageMapper.toReadDto(chatMessage));
        }
        return chatMessageReadDtos;
    }

    public Flux<String> receiveUserMessage(ChatRequest chatRequest) {
        String userMessage = chatRequest.message();
        Long userId = Long.valueOf(chatRequest.userId());

        saveChatMessage(userMessage, userId, "user");

        List<Message> history = getSpringAiHistory(userId);

        StringBuilder fullAiResponse = new StringBuilder();

        return processUserMessage(history)
                .doOnNext(chunk -> fullAiResponse.append(chunk))
                .doOnComplete(() -> {
                    saveChatMessage(fullAiResponse.toString(), userId, "ai");
                });
    }
}
