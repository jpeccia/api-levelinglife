package com.jpeccia.levelinglife.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

       // Método que atribui o título com base no nível
       public String getTitleForLevel(int level) {
        if (level >= 1 && level <= 5) {
            return "Sedentário Supremo";
        } else if (level >= 6 && level <= 10) {
            return "Ninja das Metas Ignoradas";
        } else if (level >= 11 && level <= 20) {
            return "PhD em Cochilos";
        } else if (level >= 21 && level <= 30) {
            return "Herói da Ação Zero";
        } else if (level >= 31 && level <= 40) {
            return "Lorde da Autossabotagem";
        } else if (level >= 41 && level <= 50) {
            return "Mestre da Falta de Motivação";
        } else if (level >= 51 && level <= 70) {
            return "Lorde dos Feitos Não Feitos";
        } else if (level >= 70 && level <= 75) {
            return "Herói do Prazo Apertado";
        } else if (level >= 76 && level <= 80) {
            return "Campeão da Luta Contra o Prazo";
        } else if (level >= 81 && level <= 85) {
            return "Príncipe do Café com Leite";
        } else if (level >= 86 && level <= 90) {
            return "Rei do Sucesso Tardio";
        } else if (level >= 91 && level <= 95) {
            return "Titan das Conquistas";
        } else if (level >= 96 && level <= 100) {
            return "Monarca da Meta Realizada";
        }
        return "Sem Título"; // Caso não tenha título para o nível
    }

    public User updateUser(User user){
        return userRepository.save(user);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUserProfile(String username, String newName) {
        User user = getUserByUsername(username);
        user.setName(newName);
        return userRepository.save(user);
    }
}
