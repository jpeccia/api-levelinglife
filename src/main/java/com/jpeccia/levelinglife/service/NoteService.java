package com.jpeccia.levelinglife.service;

import com.jpeccia.levelinglife.controller.NotesController.NoteNotFoundException;
import com.jpeccia.levelinglife.dto.NoteRequestDTO;
import com.jpeccia.levelinglife.dto.NoteResponseDTO;
import com.jpeccia.levelinglife.entity.Note;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.NoteRepository;
import com.jpeccia.levelinglife.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;

// Classe de Serviço
@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new RuntimeException("No authenticated user found");
        }
        User user = (User) authentication.getPrincipal();
        System.out.println("Authenticated User: " + user.getId());  // Logando o ID do usuário
        return user;
    }
    

    // Criar Nota
    public Note createNote(NoteRequestDTO noteDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
        Note note = new Note();
        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setUpdatedAt(new Date());
        note.setUser(user);
    
        return noteRepository.save(note);
    }
    public List<NoteResponseDTO> getNotes() throws Exception {
        User user = getAuthenticatedUser();
        List<Note> notes = noteRepository.findByUser(user);
    
        // Converte as notas para DTOs
        return notes.stream()
                    .map(note -> new NoteResponseDTO(
                        note.getId(), 
                        note.getTitle(), 
                        note.getContent(), 
                        note.getUpdatedAt()))
                    .collect(Collectors.toList());
    }
    // Atualizar Nota
    public Note updateNote(Long id, NoteRequestDTO noteDto) {
        User user = getAuthenticatedUser();  // Recupera o usuário autenticado
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));  // Lança exceção se nota não encontrada

        // Comparação de IDs dos usuários para verificar a permissão de atualização
        if (!existingNote.getUser().getId().equals(user.getId())) {
            throw new PermissionDeniedException("You do not have permission to update this note");
        }

        // Atualiza os campos da nota
        existingNote.setTitle(noteDto.getTitle());
        existingNote.setContent(noteDto.getContent());
        existingNote.setUpdatedAt(new Date());  // Atualiza a data de modificação

        // Salva a nota atualizada no banco de dados
        return noteRepository.save(existingNote);
    }

    // Deletar Nota
    @Transactional
    public void deleteNote(Long id) {
        User user = getAuthenticatedUser();
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
    
        // Compare os IDs diretamente
        if (!existingNote.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this note");
        }
    
        noteRepository.delete(existingNote);
    }

    public class PermissionDeniedException extends RuntimeException {
        public PermissionDeniedException(String message) {
            super(message);
        }
    }

    public class NoteNotFoundException extends RuntimeException {
        public NoteNotFoundException(String message) {
            super(message);
        }
    }
    
}
