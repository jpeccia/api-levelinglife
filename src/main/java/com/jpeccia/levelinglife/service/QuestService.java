package com.jpeccia.levelinglife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpeccia.levelinglife.entity.Quest;
import com.jpeccia.levelinglife.repository.QuestRepository;

@Service
public class QuestService {
    
    @Autowired
    private QuestRepository questRepository;

    public List<Quest> findAllQuestsByUserId(Long userId){
        return questRepository.findByUserId(userId);
    }


    public void completeQuest(Long questId){
        Quest quest = questRepository.findById(questId)
        .orElseThrow(() -> new IllegalArgumentException("Quest not exist!"));

        quest.setCompleted(true);
        questRepository.save(quest);
    }

    public void deleteQuest(Long questId){
        questRepository.deleteById(questId);
    }

}
