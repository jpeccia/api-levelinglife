package com.jpeccia.levelinglife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpeccia.levelinglife.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndRecipient(String sender, String recipient);

    // Busca conversas de ida e volta
    List<ChatMessage> findBySenderAndRecipientOrSenderAndRecipient(
            String sender1, String recipient1,
            String sender2, String recipient2);
}
