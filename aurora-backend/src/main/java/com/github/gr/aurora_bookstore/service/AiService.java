package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.aiDto.ChatRequest;
import com.github.gr.aurora_bookstore.dto.chatMessageDto.ChatMessageReadDTO;
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

    private final ChatClient baseChatClient;
    private final ChatClient toolsChatClient;
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final VectorStore vectorStore;

    public AiService(ChatClient.Builder chatClientBuilder,
            ChatMessageRepository chatMessageRepository,
            UserService userService,
            VectorStore vectorStore,
            BorealTools borealTools) {
        // Create two clients: one strictly for chat/policies, and another loaded with
        // tools.
        this.baseChatClient = chatClientBuilder.build();
        this.toolsChatClient = chatClientBuilder.defaultTools(borealTools).build();

        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
        this.vectorStore = vectorStore;
    }

    public Flux<String> processUserMessage(ChatClient client, List<Message> messages, String extraInstructions) {
        try {
            String systemText = systemPrompt.getContentAsString(StandardCharsets.UTF_8);
            String combinedSystemText = systemText;
            if (extraInstructions != null && !extraInstructions.isBlank()) {
                combinedSystemText = systemText + "\n\n" + extraInstructions;
            }

            return client.prompt()
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
        // Guardrail: Limit the context to the last 4 messages to prevent token overflow
        List<ChatMessage> lastMessages = chatMessageRepository.findTop4ByUserIdOrderByCreatedAtDesc(userId).reversed();
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

    public List<ChatMessageReadDTO> chatMessagesToDto(Long userId) {
        List<ChatMessage> lastMessages = chatMessageRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId).reversed();
        List<ChatMessageReadDTO> chatMessageReadDtos = new ArrayList<>();
        for (ChatMessage chatMessage : lastMessages) {
            chatMessageReadDtos.add(ChatMessageMapper.toReadDto(chatMessage));
        }
        return chatMessageReadDtos;
    }

    public Flux<String> receiveUserMessage(ChatRequest chatRequest, Long userId) {
        log.info("RECEIVED REQUEST: {}, user: {}", chatRequest, userId);
        String userMessage = chatRequest.message();

        saveChatMessage(userMessage, userId, "user");

        // GUARDRAIL
        String lowerMsg = userMessage.toLowerCase();
        boolean isBookIntent = lowerMsg.matches(
                ".*(livro|autor|fantasia|terror|romance|catálogo|catalogo|recomenda|gênero|genero|ficção|título|titulo).*")
                ||
                lowerMsg.matches(
                        ".*(book|author|fantasy|horror|catalog|recommend|genre|fiction|title).*");
        boolean isPolicyIntent = lowerMsg.matches(
                ".*(pagamento|pix|boleto|cartão|cartao|frete|devolução|devolucao|prazo|política|politica|contato|horário|horario|regras).*")
                ||
                lowerMsg.matches(
                        ".*(payment|card|shipping|return|policy|contact|hour|time|rule).*");

        String extraInstructions = "";
        ChatClient currentClient;

        if (isBookIntent) {
            log.info("ROUTE: BOOKS (RAG OFF, TOOLS ON)");
            extraInstructions = "[TOOL INSTRUCTIONS]\n- To specificially find books, use the 'searchBooks' tool. if you need all books use getAllBooks() tool, to get all categories, genres and authors, use 'getAllCategoriesGenresAndAuthors' tool. Extract ONLY the exact core keyword translated to English (e.g., use 'fantasy' instead of 'fantasy books'). CRITICAL RULE: You MUST integrate the exact text returned by the tool into your response. If the tool returns a list of categories, genres, or authors, you MUST output that exact list to the user. NEVER say 'we have a variety' without listing the items. When suggesting a book, ALWAYS include its title, price, and stock in your text response.";
            currentClient = toolsChatClient;
        } else if (isPolicyIntent) {
            log.info("ROUTE: POLICIES (RAG ON, TOOLS OFF)");
            List<Document> similarDocs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(userMessage).topK(2).build());
            String ragContext = similarDocs.stream().map(Document::getText).collect(Collectors.joining("\n\n"));
            extraInstructions = "[CONTEXT FROM STORE POLICIES]\n" + ragContext;
            currentClient = baseChatClient;
        } else {
            log.info("ROUTE: SMALL TALK (RAG OFF, TOOLS OFF)");
            currentClient = baseChatClient;
        }
        // END OF GUARDRAIL

        List<Message> history = getSpringAiHistory(userId);

        StringBuilder fullAiResponse = new StringBuilder();

        log.info("SENDING REQUEST TO BOREAL");
        return processUserMessage(currentClient, history, extraInstructions)
                .doOnNext(chunk -> fullAiResponse.append(chunk))
                .doOnComplete(() -> {
                    saveChatMessage(fullAiResponse.toString(), userId, "ai");
                })
                // REACTIVE GUARDRAIL: If an error occurs or returns empty, the flow is captured
                .defaultIfEmpty("Desculpe, tive um lapso de memória! Pode repetir a pergunta?")
                .onErrorResume(e -> {
                    log.error("Error in AI Stream", e);
                    return Flux
                            .just("Desculpe, ocorreu um erro técnico ao processar sua mensagem. Pode tentar de novo?");
                });
    }
}
