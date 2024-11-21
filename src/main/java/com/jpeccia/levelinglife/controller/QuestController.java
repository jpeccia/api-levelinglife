package com.jpeccia.levelinglife.controller;

import java.util.List;
import java.util.Optional;

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
import com.jpeccia.levelinglife.dto.UserProfileDTO;
import com.jpeccia.levelinglife.entity.Quest;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.infra.security.TokenService;
import com.jpeccia.levelinglife.repository.QuestRepository;
import com.jpeccia.levelinglife.repository.UserRepository;
import com.jpeccia.levelinglife.service.QuestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Listar quests por ID de usuário
        @Operation(summary = "Listar quests ativas do usuário", description = "Retorna todas as quests ativas de um usuário autenticado.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Quests retornadas com sucesso."),
                @ApiResponse(responseCode = "401", description = "Token inválido ou usuário não autenticado.")
        })
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

        // Adicionar uma nova quest
        @Operation(summary = "Adicionar uma nova quest", description = "Adiciona uma nova quest para o usuário autenticado.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Quest adicionada com sucesso."),
                @ApiResponse(responseCode = "401", description = "Token inválido ou usuário não autenticado."),
                @ApiResponse(responseCode = "400", description = "Dados inválidos para criação da quest.")
        })
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

        // Atualizar uma quest existente
        @Operation(summary = "Atualizar uma quest", description = "Atualiza uma quest existente do usuário autenticado.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Quest atualizada com sucesso."),
                @ApiResponse(responseCode = "404", description = "Quest não encontrada ou usuário não autorizado."),
                @ApiResponse(responseCode = "401", description = "Token inválido ou usuário não autenticado.")
        })
        @PutMapping("/{id}")
        public ResponseEntity<Quest> updateQuest(
                @PathVariable Long id,
                @RequestBody QuestDTO questDTO,
                HttpServletRequest request) {

            // Extrair e validar o token JWT
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            String username = tokenService.validateToken(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Buscar o usuário autenticado
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found!"));

            // Buscar a quest pelo ID e garantir que pertence ao usuário autenticado
            Quest quest = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Quest not found!"));

            if (!quest.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Atualizar os detalhes da quest com os dados do DTO
            quest.setTitle(questDTO.getTitle());
            quest.setDescription(questDTO.getDescription());
            quest.setType(questDTO.getType());

            // Salvar as alterações
            repository.save(quest);

            return ResponseEntity.ok(quest);
        }

        // Marcar uma quest como completa
        @Operation(summary = "Marcar uma quest como completa", description = "Marca uma quest como completada.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Quest marcada como completada."),
                @ApiResponse(responseCode = "404", description = "Quest não encontrada.")
        })
        @PutMapping("/{id}/complete")
        public ResponseEntity<UserProfileDTO> completeQuest(@PathVariable Long id) {
            questService.completeQuest(id);

            // Busca o usuário atualizado
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        
            // Retorna os dados do usuário atualizado
            UserProfileDTO userProfileDTO = new UserProfileDTO(
                user.getName(),
                user.getUsername(),
                user.getTitle(),
                user.getEmail(),
                user.getLevel(),
                user.getXp(),
                user.getProfilePicture()
            );
        
            return ResponseEntity.ok(userProfileDTO);
        }

    // Deletar uma quest por ID
    @Operation(summary = "Deletar uma quest", description = "Deleta uma quest existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quest deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Quest não encontrada.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuest(@PathVariable Long id){
        questService.deleteQuest(id);
        return ResponseEntity.ok().build();
    }

    // Endpoint para obter quests com suas datas de expiração para o calendário
    @Operation(summary = "Obter quests para o calendário", description = "Retorna as quests de um usuário, com foco nas datas de expiração para o calendário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quests retornadas com sucesso."),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @GetMapping("/calendar")
    public ResponseEntity<List<Quest>> getCalendarQuests() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        List<Quest> quests = repository.findByUserId(user.getId());

        return ResponseEntity.ok(quests);
    }
}
