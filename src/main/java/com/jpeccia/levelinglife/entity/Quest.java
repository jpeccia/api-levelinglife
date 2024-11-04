package com.jpeccia.levelinglife.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "quests")
@Getter
@Setter
public class Quest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int xp;

    @Enumerated(EnumType.STRING)
    private QuestType type;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime completedAt; 


    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Quest() {
        this.createdAt = LocalDateTime.now();
        this.expiresAt = calculateExpiryDate(); // Define a data de expiração ao criar a quest
    }

    // Calcula a data de expiração com base na frequência da quest
    public LocalDateTime calculateExpiryDate() {
        switch (this.type) {
            case DAILY:
                return this.createdAt.plusDays(1); // Expira em 24 horas
            case WEEKLY:
                return this.createdAt.plusWeeks(1); // Expira em 7 dias
            case MONTHLY:
                return this.createdAt.plusMonths(1); // Expira em 1 mês
            default:
                return this.createdAt;
        }

}
}
