package com.jpeccia.levelinglife.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import com.jpeccia.levelinglife.entity.ChatMessage;
import com.jpeccia.levelinglife.repository.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatMessageRepository chatMessageRepository;

    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    // Envio de mensagens (WebSocket)
    @MessageMapping("/send-message") // Rota que recebe mensagens
    @SendToUser("/queue/reply") // Envia diretamente para o usuário destinatário
    public ChatMessage sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(message); // Salva no banco de dados
        return message; // Retorna a mensagem ao destinatário
    }

    // Recuperação de histórico (REST)
    @GetMapping("/history/{user1}/{user2}")
    public List<ChatMessage> getChatHistory(@PathVariable String user1, @PathVariable String user2) {
        // Busca mensagens enviadas por user1 para user2 e vice-versa
        return chatMessageRepository.findBySenderAndRecipient(user1, user2);
    }
}
