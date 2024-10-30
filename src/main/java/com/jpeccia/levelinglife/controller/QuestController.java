package com.jpeccia.levelinglife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.dto.QuestDTO;
import com.jpeccia.levelinglife.entity.Quest;
import com.jpeccia.levelinglife.repository.QuestRepository;
import com.jpeccia.levelinglife.repository.UserRepository;
import com.jpeccia.levelinglife.service.QuestService;

@RestController
@RequestMapping("/quests")
public class QuestController {
    
    @Autowired
    private QuestService questService;

    @Autowired 
    QuestRepository repository;

    @Autowired
    UserRepository userRepository;

    // Listar quests por ID de usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity <List<Quest>> getQuestsByUserId(@PathVariable Long userId){
        List<Quest> quests = questService.findAllQuestsByUserId(userId);
        return ResponseEntity.ok(quests);
    }

    // Adicionar uma nova quest
    @PostMapping("/add")
    public ResponseEntity<Quest> addQuest(@RequestBody QuestDTO body) {
        Quest newQuest = questService.createQuest(body);
        return ResponseEntity.ok(newQuest);
    }

    // Marcar uma quest como completa
    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completeQuest(@PathVariable Long id){
        questService.completeQuest(id);
        return ResponseEntity.ok().build();
    }

    // Deletar uma quest por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuest(@PathVariable Long id){
        questService.deleteQuest(id);
        return ResponseEntity.ok().build();
    }
}
