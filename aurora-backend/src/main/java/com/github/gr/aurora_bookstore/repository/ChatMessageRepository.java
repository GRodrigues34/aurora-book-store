package com.github.gr.aurora_bookstore.repository;

import com.github.gr.aurora_bookstore.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    List<ChatMessage> findTop4ByUserIdOrderByCreatedAtDesc(Long userId);
}
