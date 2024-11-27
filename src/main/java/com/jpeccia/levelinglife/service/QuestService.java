package com.jpeccia.levelinglife.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UserService userService;

    // Método que será chamado periodicamente
    @Scheduled(fixedRate = 60000) // Executa a cada 60 segundos
    @Transactional
    public void removeExpiredQuests() {
        try {
            LocalDateTime now = LocalDateTime.now();
            // Busca e remove as quests expiradas
            questRepository.deleteAllByExpiresAtBefore(now);
        } catch (Exception e) {
            // Log do erro para depuração
            System.err.println("Erro ao remover quests expiradas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Calcula o XP necessário para o próximo nível
    public int calculateXpForNextLevel(int level) {
        return level * 800; // Exemplo: cada nível requer 800 XP a mais
    }

    // Listar quests por usuário
    public List<Quest> findAllQuestsByUserId(Long userId){
        return questRepository.findByUserId(userId);
    }

    public Quest createQuest(QuestDTO body, Long userId, LocalDate dueDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    
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
                newQuest.setXp(1000);
                break;
            case MONTHLY:
                newQuest.setXp(4500);
                break;
            default:
                newQuest.setXp(0); // Define XP padrão para tipos desconhecidos
                break;
        }
    
        // Calcular e definir a data de expiração com base no tipo de quest
        LocalDateTime now = LocalDateTime.now();
        switch (newQuest.getType()) {
            case DAILY:
                newQuest.setExpiresAt(now.plusDays(1)); // Expira em 24 horas
                break;
            case WEEKLY:
                newQuest.setExpiresAt(now.plusWeeks(1)); // Expira em 7 dias
                break;
            case MONTHLY:
                newQuest.setExpiresAt(now.plusMonths(1)); // Expira em 1 mês
                break;
            default:
                newQuest.setExpiresAt(null); // Não define expiração para tipos desconhecidos
                break;
        }
    
        // Adicionando a data de vencimento (dueDate) recebida do front-end
        // A data de vencimento é passada pelo front-end como parte do corpo da requisição
        if (body.getDueDate() != null) {
            newQuest.setDueDate(body.getDueDate()); // Define a data de vencimento para a quest
        }
    
        newQuest.setUser(user); // Definindo o usuário na nova quest
    
        // Salva e retorna a quest
        return questRepository.save(newQuest);
    }

    // Marcar quest como completa
    public void completeQuest(Long questId) {
        // Verifica se a quest existe no banco de dados
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new RuntimeException("Quest not found"));
    
        // Verifica se a quest já foi concluída
        if (quest.getCompletedAt() != null) {
            throw new RuntimeException("Quest already completed");
        }
    
        // Define completedAt com a data e hora atuais
        quest.setCompletedAt(LocalDateTime.now());
    
        // Obter o usuário associado à quest
        User user = quest.getUser();
        int xpEarned = quest.getXp();
    
        // Verifique se a quantidade de XP é válida para evitar abusos
        if (xpEarned <= 0) {
            throw new RuntimeException("Invalid XP value for quest completion");
        }
    
        // Adiciona o XP ganho ao usuário
        user.setXp(user.getXp() + xpEarned);
    
        // Lógica para subir de nível enquanto o usuário tiver XP suficiente
        while (user.getXp() >= calculateXpForNextLevel(user.getLevel())) {
            int xpForNextLevel = calculateXpForNextLevel(user.getLevel());
    
            // Subtrai o XP necessário para subir de nível
            user.setXp(user.getXp() - xpForNextLevel);
    
            // Incrementa o nível do usuário
            user.setLevel(user.getLevel() + 1);
    
            // Atualiza o título do usuário com base no novo nível
            user.setTitle(userService.getTitleForLevel(user.getLevel()));
        }
    
        // Salva o usuário e a quest com transações para garantir consistência de dados
        try {
            // Use transação para garantir atomicidade
            userRepository.save(user);
            questRepository.save(quest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user or quest data", e);
        }
    }

    // Deletar quest
    public void deleteQuest(Long questId){
        questRepository.deleteById(questId);
    }

}
