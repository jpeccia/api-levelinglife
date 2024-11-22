package com.jpeccia.levelinglife.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender; // Username de quem enviou
    private String recipient; // Username de quem recebe
    private String content; // Conteúdo da mensagem
    private LocalDateTime timestamp; // Data e hora
}
