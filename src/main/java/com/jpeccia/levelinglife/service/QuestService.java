package com.jpeccia.levelinglife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpeccia.levelinglife.dto.QuestDTO;
import com.jpeccia.levelinglife.entity.Quest;
import com.jpeccia.levelinglife.repository.QuestRepository;

@Service
public class QuestService {
    
    @Autowired
    private QuestRepository questRepository;

    // Listar quests por usuário
    public List<Quest> findAllQuestsByUserId(Long userId){
        return questRepository.findByUserId(userId);
    }

     // Criar nova quest com definição automática de XP
    public Quest createQuest(QuestDTO body) {
        Quest newQuest = new Quest();
        newQuest.setTitle(body.getTitle());
        newQuest.setDescription(body.getDescription());
        newQuest.setType(body.getType());

        // Definir XP com base no tipo de quest
        switch (newQuest.getType()) {
            case DAILY:
                newQuest.setXp(100);
                break;
            case WEEKLY:
                newQuest.setXp(500);
                break;
            case MONTHLY:
                newQuest.setXp(4500);
                break;
            default:
                newQuest.setXp(0); // Define XP padrão para tipos desconhecidos
                break;
        }

        return questRepository.save(newQuest); // Salva e retorna a quest
    }

    // Marcar quest como completa
    public void completeQuest(Long questId){
        Quest quest = questRepository.findById(questId)
        .orElseThrow(() -> new IllegalArgumentException("Quest not exist!"));

        quest.setCompleted(true);
        questRepository.save(quest);
    }

    // Deletar quest
    public void deleteQuest(Long questId){
        questRepository.deleteById(questId);
    }

}
