package com.jpeccia.levelinglife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRankingDTO {
    private String name;
    private String username;
    private String title;
    private int level;
    private int xp;
    private String profilePicture;
}