package com.jpeccia.levelinglife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRankingDTO {
    private String name;
    private String title;
    private String username;
    private int level;
    private int xp;
    private byte[] profilePicture;
}