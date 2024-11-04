package com.jpeccia.levelinglife.controller;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.dto.UserProfileDTO;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

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
}
