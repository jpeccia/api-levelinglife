package com.jpeccia.levelinglife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestTestDTO {
    
    private String title;
    private String description;
    private int xp;
    private String type; // DAILY, WEEKLY, MONTHLY

}
