package com.jpeccia.levelinglife.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.jpeccia.levelinglife.entity.ChatMessage;
import com.jpeccia.levelinglife.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatMessageRepository chatMessageRepository;

    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    // Envio de mensagens via WebSocket
    @MessageMapping("/send-message")
    @SendToUser("/queue/reply")
    public ChatMessage sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());

        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Mensagem não pode ser vazia!");
        }

        // Salva a mensagem no banco de dados
        chatMessageRepository.save(message);

        return message;
    }

    // Recuperação de histórico entre dois usuários
    @GetMapping("/history/{user1}/{user2}")
    public List<ChatMessage> getChatHistory(@PathVariable String user1, @PathVariable String user2) {
        return chatMessageRepository.findBySenderAndRecipientOrSenderAndRecipient(user1, user2, user2, user1);
    }
}
