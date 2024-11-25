package com.jpeccia.levelinglife.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Date updatedAt;
}
