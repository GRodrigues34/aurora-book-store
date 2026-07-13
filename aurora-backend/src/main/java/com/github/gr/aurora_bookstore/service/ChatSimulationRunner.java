package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.aiDto.ChatRequest;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.repository.ChatMessageRepository;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SIMULADOR DE CHAT PARA LEITURA DE LOGS
 * 
 * Para desativar esse simulador no futuro e não atrasar a inicialização do seu
 * app,
 * basta comentar a anotação @Component abaixo.
 */
@Profile("!test")
@Component
@RequiredArgsConstructor
public class ChatSimulationRunner implements CommandLineRunner {

    private final AiService aiService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void run(String... args) {
        System.out.println("\n=========================================================");
        System.out.println("BOREAL CHAT SIMULATION");
        System.out.println("=========================================================\n");

        User user = new User();
        user.setUsername("test_conversational_user");
        user.setEmail("test_" + java.util.UUID.randomUUID().toString().substring(0, 8) + "@aurora.com");
        user.setPassword("password123");
        user.setRole("USER");

        User testUser = userRepository.save(user);

        StringBuilder fullChat = new StringBuilder();
        fullChat.append("\n=========================================================\n");
        fullChat.append("               FULL BOREAL CHAT TRANSCRIPT               \n");
        fullChat.append("=========================================================\n\n");

        chat("Olá", testUser, fullChat);
        chat("Quem é você?", testUser, fullChat);
        chat("Vocês tem livros de fantasia?", testUser, fullChat);
        chat("Quais outros livros vocês possuem?", testUser, fullChat);
        chat("E quais as categorias de livros que vocês possuem?", testUser, fullChat);
        chat("Otimo! quais as formas de pagamento que vocês aceitam?", testUser, fullChat);
        chat("Entendido! quais as outras politicas da empresa?", testUser, fullChat);

        System.out.println(fullChat.toString());

        System.out.println("=========================================================");
        System.out.println("SIMULAÇÃO CONCLUIDA");
        System.out.println("=========================================================\n");
    }

    private void chat(String message, User testUser, StringBuilder fullChat) {
        System.out.println("🔄 Processando mensagem: '" + message + "'...");

        ChatRequest request = new ChatRequest(message, String.valueOf(testUser.getId()));

        String fullResponse = aiService.receiveUserMessage(request)
                .reduce(String::concat)
                .block();

        fullChat.append("🧑‍💻 USER: ").append(message).append("\n");
        fullChat.append("🤖 BOREAL: ").append(fullResponse).append("\n");
        fullChat.append("---------------------------------------------------------\n");
    }
}
