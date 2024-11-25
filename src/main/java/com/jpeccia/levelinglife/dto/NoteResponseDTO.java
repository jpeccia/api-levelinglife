package com.jpeccia.levelinglife.dto;

import java.util.Date;

public record NoteResponseDTO( Long id, String title, String content, Date updatedAt) {
    
}
