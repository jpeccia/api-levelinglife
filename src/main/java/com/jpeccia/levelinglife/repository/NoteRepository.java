package com.jpeccia.levelinglife.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpeccia.levelinglife.entity.Note;
import com.jpeccia.levelinglife.entity.User;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser(User user);  // Buscar notas do usuário
}
