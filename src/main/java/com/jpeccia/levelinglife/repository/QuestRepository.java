package com.jpeccia.levelinglife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpeccia.levelinglife.entity.Quest;

public interface QuestRepository extends JpaRepository<Quest, Long>{

    List<Quest> findByUserId(Long userId);
    
}
