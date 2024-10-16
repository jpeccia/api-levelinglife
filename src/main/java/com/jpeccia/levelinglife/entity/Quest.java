package com.jpeccia.levelinglife.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
    
    private Long id;

    private String title;

    private String description;

    private int xp;

    private QuestType type;

    private boolean isCompleted = false;

    private LocalDateTime createdAt = LocalDateTime.now();


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
