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
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import com.github.gr.aurora_bookstore.tool.BorealTools;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiService {

    @Value("classpath:system-prompt.txt")
    Resource systemPrompt;

    @Value("classpath:policies.md")
    Resource policies;

    private final ChatClient chatClient;
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final VectorStore vectorStore;

    private final BorealTools borealTools;

    public AiService(ChatClient.Builder chatClientBuilder,
            ChatMessageRepository chatMessageRepository,
            UserService userService,
            VectorStore vectorStore,
            BorealTools borealTools) {
        this.chatClient = chatClientBuilder
                .build();
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
        this.vectorStore = vectorStore;
        this.borealTools = borealTools;
    }

    public Flux<String> processUserMessage(List<Message> messages, String context) {
        try {
            String systemText = systemPrompt.getContentAsString(StandardCharsets.UTF_8);
            String combinedSystemText = systemText;
            if (context != null && !context.isBlank()) {
                combinedSystemText = systemText + "\n\nContext from Store Policies:\n" + context;
            }

            return chatClient.prompt()
                    .system(combinedSystemText)
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
        log.info("RECEIVED REQUEST: {}", chatRequest);
        String userMessage = chatRequest.message();
        Long userId = Long.valueOf(chatRequest.userId());

        saveChatMessage(userMessage, userId, "user");

        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userMessage)
                        .topK(2)
                        .build());
        log.info("SIMILAR DOCS FOUND: {}", similarDocs);

        String context = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        log.info("CONTEXT GATHERED: {}", context);

        List<Message> history = getSpringAiHistory(userId);
        log.info("CHAT HISTORY RETRIEVED: {}", history);

        StringBuilder fullAiResponse = new StringBuilder();

        log.info("SENDING CONTEXT AND HISTORY AS REQUEST TO BOREAL");
        return processUserMessage(history, context)
                .doOnNext(chunk -> fullAiResponse.append(chunk))
                .doOnComplete(() -> {
                    saveChatMessage(fullAiResponse.toString(), userId, "ai");
                });
    }
}
