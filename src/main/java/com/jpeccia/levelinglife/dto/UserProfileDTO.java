package com.jpeccia.levelinglife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class UserProfileDTO {
    private String name;
    private String username;
    private String title;
    private String email;
    private int level;
    private int xp;
    private String profilePicture;
}