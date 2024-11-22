package com.jpeccia.levelinglife.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.jpeccia.levelinglife.entity.Note;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.NoteRepository;
import com.jpeccia.levelinglife.repository.UserRepository;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    // Obter todas as notas do usuário logado
    @GetMapping
    public List<Note> getNotes(Authentication authentication) {
        String username = authentication.getName();  // Obtém o nome de usuário do login
        User user = userRepository.findByUsernameAndNote(username);
        
        return noteRepository.findByUser(user);
    }

    // Criar uma nova nota associada ao usuário logado
    @PostMapping
    public Note createNote(@RequestBody Note note, Authentication authentication) {
        String username = authentication.getName();  // Obtém o nome de usuário do login
        User user = userRepository.findByUsernameAndNote(username);
        note.setUser(user);
        note.setUpdatedAt(new Date());
        return noteRepository.save(note);
    }

    // Atualizar uma nota existente
    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @RequestBody Note note, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsernameAndNote(username);
        note.setUser(user);
        note.setId(id);
        note.setUpdatedAt(new Date());
        return noteRepository.save(note);
    }

    // Deletar uma nota
    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id) {
        noteRepository.deleteById(id);
    }
}
