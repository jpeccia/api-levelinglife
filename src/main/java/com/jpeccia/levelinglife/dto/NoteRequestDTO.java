package com.jpeccia.levelinglife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteRequestDTO {
    private String title;
    private String content;
} 