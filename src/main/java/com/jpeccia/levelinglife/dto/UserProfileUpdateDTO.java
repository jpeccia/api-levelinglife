package com.jpeccia.levelinglife.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    private String name;
    private String currentPassword; // Senha atual para autenticação
    private String newPassword;     // Nova senha opcional
    private String newEmail;        // Novo email opcional
    private String profilePicture; // Alteração para receber o arquivo
}