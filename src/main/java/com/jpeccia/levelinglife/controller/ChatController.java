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

    // Envio de mensagens via WebSocket
    @MessageMapping("/send-message") // Rota para envio
    @SendToUser("/queue/reply")      // Resposta direta ao destinatário
    public ChatMessage sendMessage(ChatMessage message) {
        // Adiciona timestamp
        message.setTimestamp(LocalDateTime.now());

        // Validação: Não permitir mensagens vazias
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Mensagem não pode ser vazia!");
        }

        // Salva no banco de dados
        chatMessageRepository.save(message);

        return message; // Retorna a mensagem
    }

    // Recuperação de histórico entre dois usuários
    @GetMapping("/history/{user1}/{user2}")
    public List<ChatMessage> getChatHistory(@PathVariable String user1, @PathVariable String user2) {
        // Busca mensagens de ambos os lados
        return chatMessageRepository.findBySenderAndRecipientOrSenderAndRecipient(user1, user2, user2, user1);
    }
}
