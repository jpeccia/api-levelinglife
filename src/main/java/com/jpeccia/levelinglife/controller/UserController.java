package com.jpeccia.levelinglife.controller;
import java.util.List;


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
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //Endpoint pra ver o perfil do usuario
    @GetMapping("/profile")
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

    @PostMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody UserProfileUpdateDTO body) {
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

}
