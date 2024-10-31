package com.jpeccia.levelinglife.dto;

import com.jpeccia.levelinglife.entity.QuestType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestResponseDTO {
    
    public String title;
    public String description;
    public QuestType type;
    public int xp;
}
