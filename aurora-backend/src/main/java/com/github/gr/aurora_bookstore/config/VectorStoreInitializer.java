package com.github.gr.aurora_bookstore.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class VectorStoreInitializer implements CommandLineRunner {

    private final VectorStore vectorStore;

    @Value("classpath:policies.md")
    private Resource policies;

    public VectorStoreInitializer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking Vector Store");

        try {
            List<Document> existingDocs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query("policies")
                            .topK(1)
                            .build());

            if (!existingDocs.isEmpty()) {
                return;
            }

            log.info("Vector Store empty");

            if (policies == null || !policies.exists()) {
                log.error("policies.md resource not found!");
                return;
            }

            String content = policies.getContentAsString(StandardCharsets.UTF_8);

            List<Document> documents = Arrays.stream(content.split("\n\n"))
                    .map(String::trim)
                    .filter(paragraph -> !paragraph.isEmpty())
                    .map(Document::new)
                    .toList();

            log.info("Generated {} document chunks from policies.md.", documents.size());

            vectorStore.add(documents);
            log.info("Vector Store populated");

        } catch (Exception e) {
            log.error("Failed to initialize Vector Store: {}", e.getMessage(), e);
        }
    }
}
