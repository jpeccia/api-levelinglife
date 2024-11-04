package com.jpeccia.levelinglife.dto;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    private String name;
    private String newPassword; // Nova senha opcional
    private String newEmail;    // Novo email opcional
    private String profilePicture;
}