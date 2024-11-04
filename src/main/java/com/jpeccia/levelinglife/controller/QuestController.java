package com.jpeccia.levelinglife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.dto.QuestDTO;
import com.jpeccia.levelinglife.entity.Quest;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.infra.security.TokenService;
import com.jpeccia.levelinglife.repository.QuestRepository;
import com.jpeccia.levelinglife.repository.UserRepository;
import com.jpeccia.levelinglife.service.QuestService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/quests")
public class QuestController {
    
    @Autowired
    private QuestService questService;

    @Autowired 
    QuestRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    // Listar quests por ID de usuário
    @GetMapping("/")
    public ResponseEntity<List<Quest>> getUserQuests(@RequestHeader("Authorization") String authHeader) {
        // Extrair o token JWT do cabeçalho e validá-lo
        String token = authHeader.replace("Bearer ", "");
        String username = tokenService.validateToken(token);
        
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Buscar o usuário no banco de dados
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));


        // Recuperar todas as quests associadas ao usuário
        List<Quest> activeQuests = repository.findByUserIdAndCompletedAtIsNull(user.getId());
        return ResponseEntity.ok(activeQuests);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Quest>> getCompletedQuests() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Quest> completedQuests = repository.findByUserIdAndCompletedAtIsNotNull(user.getId());
        return ResponseEntity.ok(completedQuests);
    }

    // Adicionar uma nova quest
    @PostMapping("/")
    public ResponseEntity<Quest> addQuest(@RequestBody QuestDTO body, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        System.out.println("Received token for validation: " + token);
    
        String username = tokenService.validateToken(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("User not found!"));

        Quest newQuest = questService.createQuest(body, user.getId());
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
