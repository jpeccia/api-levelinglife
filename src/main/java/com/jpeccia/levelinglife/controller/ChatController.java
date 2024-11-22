package com.jpeccia.levelinglife.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.jpeccia.levelinglife.entity.ChatMessage;

@Controller
public class ChatController {

    @MessageMapping("/send-message") // Rota que recebe mensagens
    @SendTo("/topic/messages") // Canal para onde a mensagem será enviada
    public ChatMessage sendMessage(ChatMessage message) {
        return message; // Envia a mensagem recebida para todos os usuários conectados
    }
}
