package com.jpeccia.levelinglife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRankingDTO {
    private String name;
    private int level;
    private String profilePicture;
}