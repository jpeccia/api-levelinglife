package com.jpeccia.levelinglife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpeccia.levelinglife.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndRecipient(String sender, String recipient);
    List<ChatMessage> findByRecipientAndSender(String recipient, String sender);
}
