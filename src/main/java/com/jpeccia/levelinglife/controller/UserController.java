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

import com.jpeccia.levelinglife.dto.PasswordValidationDTO;
import com.jpeccia.levelinglife.dto.UserProfileDTO;
import com.jpeccia.levelinglife.dto.UserProfileUpdateDTO;
import com.jpeccia.levelinglife.dto.UserRankingDTO;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

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
            user.getUsername(),
            user.getTitle(),
            user.getEmail(),
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
    public ResponseEntity<String> updateProfile(@RequestBody @Valid @Parameter(description = "Dados para atualização do perfil do usuário.") UserProfileUpdateDTO body, UserProfileDTO verify) {
        // Obter o usuário autenticado do contexto de segurança
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Verificar se o usuário autenticado corresponde ao usuário da solicitação
        if (!authenticatedUser.getUsername().equals(verify.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para atualizar este perfil.");
        }

        // Atualizar nome
        if (body.getName() != null) {
            authenticatedUser.setName(body.getName());
        }

        // Atualiza a URL da foto de perfil se fornecido
        if (body.getProfilePicture() != null && !body.getProfilePicture().isEmpty()) {
            // Adicione a verificação de tamanho de arquivo e tipo, se necessário
            if (body.getProfilePicture().length() > 1048576) { // 1 MB limit (por exemplo)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A foto de perfil excede o tamanho permitido de 1 MB.");
            }
            authenticatedUser.setProfilePicture(body.getProfilePicture());
        }

        // Validação de senha atual para atualização de email e senha
        if ((body.getNewEmail() != null || body.getNewPassword() != null) && body.getCurrentPassword() != null) {
            // Verificar se a senha atual está correta
            if (!passwordEncoder.matches(body.getCurrentPassword(), authenticatedUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Senha atual incorreta.");
            }

            // Atualizar email se solicitado
            if (body.getNewEmail() != null) {
                // Verificar se o novo email está no formato correto
                if (!body.getNewEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O formato do novo email é inválido.");
                }
                authenticatedUser.setEmail(body.getNewEmail());
            }

            // Atualizar senha se solicitado
            if (body.getNewPassword() != null) {
                // Validar a força da senha (por exemplo, pelo menos 8 caracteres, incluindo números e caracteres especiais)
                if (!body.getNewPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A nova senha deve ter pelo menos 8 caracteres, incluindo números e caracteres especiais.");
                }
                authenticatedUser.setPassword(passwordEncoder.encode(body.getNewPassword()));
            }
        }

        userRepository.save(authenticatedUser); // Salvar as atualizações do usuário
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
                .map(user -> new UserRankingDTO(user.getName(), user.getUsername(), user.getTitle(), user.getLevel(), user.getXp(), user.getProfilePicture()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ranking);
    }

    @PostMapping("/check-password")
    public ResponseEntity<String> checkPassword(@RequestBody @Parameter(description = "Senha atual para validação.") PasswordValidationDTO body) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
    if (!passwordEncoder.matches(body.currentPassword(), user.getPassword())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Senha atual incorreta.");
    }
    
    return ResponseEntity.ok("Senha válida.");
}

}
