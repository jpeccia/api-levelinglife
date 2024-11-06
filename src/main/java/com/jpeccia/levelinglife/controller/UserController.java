package com.jpeccia.levelinglife.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.dto.UserProfileDTO;
import com.jpeccia.levelinglife.dto.UserProfileUpdateDTO;
import com.jpeccia.levelinglife.dto.UserRankingDTO;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    //Endpoint pra ver o perfil do usuario
    @Operation(summary = "Visualiza o perfil do usuário", description = "Retorna as informações do perfil do usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil do usuário retornado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    @GetMapping("/")
    public ResponseEntity<UserProfileDTO> getProfile() {
        // Obter o usuário autenticado do contexto de segurança
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Construir o UserProfileDTO com as informações do usuário
        UserProfileDTO profile = new UserProfileDTO(
            user.getName(),
            user.getLevel(),
            user.getXp(),
            user.getProfilePicture()
        );

        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Atualiza o perfil do usuário", description = "Permite que o usuário atualize seu nome, foto de perfil, email ou senha.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Campos inválidos ou faltando."),
            @ApiResponse(responseCode = "403", description = "Senha atual incorreta.")
    })
    @PostMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody @Parameter(description = "Dados para atualização do perfil do usuário.") UserProfileUpdateDTO body) {
        // Obter o usuário autenticado do contexto de segurança
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Atualizar nome e foto de perfil diretamente
        if (body.getName() != null) user.setName(body.getName());
        if (body.getProfilePicture() != null) user.setProfilePicture(body.getProfilePicture());

        // Validação de senha atual para atualização de email e senha
        if ((body.getNewEmail() != null || body.getNewPassword() != null) && body.getCurrentPassword() != null) {
            // Verificar se a senha atual está correta
            if (!passwordEncoder.matches(body.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Senha atual incorreta.");
            }
            
            // Atualizar email se solicitado
            if (body.getNewEmail() != null) user.setEmail(body.getNewEmail());
            
            // Atualizar senha se solicitado
            if (body.getNewPassword() != null) user.setPassword(passwordEncoder.encode(body.getNewPassword()));
        }

        userRepository.save(user); // Salvar as atualizações do usuário
        return ResponseEntity.ok("Perfil atualizado com sucesso.");
    }

    @Operation(summary = "Ranking de usuários", description = "Retorna os 10 usuários com maior nível.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking de usuários retornado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Falha ao obter o ranking.")
    })
    @GetMapping("/ranking")
    public ResponseEntity<List<UserRankingDTO>> getTopLevelUsers() {
        List<User> topUsers = userRepository.findTop10ByOrderByLevelDesc();

        // Converte a lista de User para UserRankingDTO, incluindo a foto de perfil
        List<UserRankingDTO> ranking = topUsers.stream()
                .map(user -> new UserRankingDTO(user.getName(), user.getLevel(), user.getProfilePicture()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ranking);
    }

}
