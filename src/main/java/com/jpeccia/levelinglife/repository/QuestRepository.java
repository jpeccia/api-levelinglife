package com.jpeccia.levelinglife.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpeccia.levelinglife.entity.Quest;

public interface QuestRepository extends JpaRepository<Quest, Long>{

    List<Quest> findByUserId(Long userId);
    
        // Quests não concluídas
    List<Quest> findByUserIdAndCompletedAtIsNull(Long userId);

        // Quests concluídas
    List<Quest> findByUserIdAndCompletedAtIsNotNull(Long userId);

    void deleteAllByExpiresAtBefore(LocalDateTime expiryDate);

}
