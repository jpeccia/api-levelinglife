package com.jpeccia.levelinglife.repository;
import com.jpeccia.levelinglife.entity.Note;
import com.jpeccia.levelinglife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // Buscar notas pelo usuário
    List<Note> findByUser(User user);
    
    // Buscar notas pelo usuário e título ou conteúdo
    List<Note> findByUserAndContentContaining(User user, String content);
}
