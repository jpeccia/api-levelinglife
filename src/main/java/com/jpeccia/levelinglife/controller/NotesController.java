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
        String username = authentication.getName(); // Obtém o nome do usuário logado
        User user = userRepository.findByUsername(username)
                                   .orElseThrow(() -> new RuntimeException("User not found"));
        return noteRepository.findByUser(user);
    }
    

    // Criar uma nova nota associada ao usuário logado
    @PostMapping
    public Note createNote(@RequestBody Note note, Authentication authentication) {
        String username = authentication.getName();  // Obtém o nome de usuário do login
        User user = userRepository.findByUsername(username)
                                   .orElseThrow(() -> new RuntimeException("User not found"));
        note.setUser(user);
        note.setUpdatedAt(new Date());
        return noteRepository.save(note);
    }

    @GetMapping("/search")
    public List<Note> searchNotes(@RequestParam String content, Authentication authentication) {
    String username = authentication.getName(); // Obtém o nome do usuário logado
    User user = userRepository.findByUsername(username)
                               .orElseThrow(() -> new RuntimeException("User not found"));
    return noteRepository.findByUserAndContentContaining(user, content);
    }

    // Atualizar uma nota existente
    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @RequestBody Note note, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                                   .orElseThrow(() -> new RuntimeException("User not found"));
    
        Note existingNote = noteRepository.findById(id)
                                          .orElseThrow(() -> new RuntimeException("Note not found"));
    
        // Verifica se a nota pertence ao usuário logado
        if (!existingNote.getUser().equals(user)) {
            throw new RuntimeException("You do not have permission to update this note");
        }
    
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setUpdatedAt(new Date());
        return noteRepository.save(existingNote);
    }
    

    // Deletar uma nota
    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                                   .orElseThrow(() -> new RuntimeException("User not found"));
    
        Note existingNote = noteRepository.findById(id)
                                          .orElseThrow(() -> new RuntimeException("Note not found"));
    
        if (!existingNote.getUser().equals(user)) {
            throw new RuntimeException("You do not have permission to delete this note");
        }
    
        noteRepository.delete(existingNote);
    }
}
