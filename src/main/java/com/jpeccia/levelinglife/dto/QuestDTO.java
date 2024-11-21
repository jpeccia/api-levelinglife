package com.jpeccia.levelinglife.dto;

import java.time.LocalDate;

import com.jpeccia.levelinglife.entity.QuestType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestDTO {
    
    public String title;
    public String description;
    public QuestType type;
    public int xp;
    private LocalDate dueDate;

}
