package com.jpeccia.levelinglife.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpeccia.levelinglife.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Adicione métodos customizados, se necessário
}
