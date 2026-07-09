package com.github.gr.aurora_bookstore;

import org.mockito.Mockito;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestAiConfig {

    @Bean
    public VectorStore vectorStore() {
        return Mockito.mock(VectorStore.class);
    }
}
