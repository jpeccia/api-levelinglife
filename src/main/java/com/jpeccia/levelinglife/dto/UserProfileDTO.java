package com.jpeccia.levelinglife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String name;
    private int level;
    private int xp;
    private String profilePicture;
}