package com.jpeccia.levelinglife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpeccia.levelinglife.dto.QuestDTO;
import com.jpeccia.levelinglife.entity.Quest;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.QuestRepository;
import com.jpeccia.levelinglife.repository.UserRepository;

@Service
public class QuestService {
    
    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private UserRepository userRepository;


    // Calcula o XP necessário para o próximo nível
    public int calculateXpForNextLevel(int level) {
        return level * 600; // Exemplo: cada nível requer 600 XP a mais
    }

    // Listar quests por usuário
    public List<Quest> findAllQuestsByUserId(Long userId){
        return questRepository.findByUserId(userId);
    }

     // Criar nova quest com definição automática de XP
    public Quest createQuest(QuestDTO body, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));

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

        newQuest.setUser(user); // Definindo o usuário na nova quest

        return questRepository.save(newQuest); // Salva e retorna a quest
    }

    // Marcar quest como completa
    public void completeQuest(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new RuntimeException("Quest not found"));
        if (!quest.isCompleted()) {
            quest.setCompleted(true); // Marcar como completa
            User user = quest.getUser(); // Obter o usuário da quest
            user.setXp(user.getXp() + quest.getXp()); // Adicionar XP ao usuário
            userRepository.save(user); // Salvar o usuário com novo XP
            questRepository.save(quest); // Atualizar a quest como completa
        }
    }

    // Deletar quest
    public void deleteQuest(Long questId){
        questRepository.deleteById(questId);
    }

}
