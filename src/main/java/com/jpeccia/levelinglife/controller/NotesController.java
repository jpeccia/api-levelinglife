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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "Gerenciamento de notas dos usuários")
public class NotesController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obter todas as notas do usuário logado.
     *
     * @param authentication Dados do usuário autenticado.
     * @return Lista de notas do usuário.
     */
    @GetMapping
    @Operation(summary = "Obter notas", description = "Retorna todas as notas associadas ao usuário logado.")
    public List<Note> getNotes(Authentication authentication) {
        String username = authentication.getName(); // Obtém o nome do usuário logado
        User user = userRepository.findByUsername(username)
                                   .orElseThrow(() -> new RuntimeException("User not found"));
        return noteRepository.findByUser(user);
    }

    /**
     * Criar uma nova nota associada ao usuário logado.
     *
     * @param note Nova nota a ser criada.
     * @param authentication Dados do usuário autenticado.
     * @return A nota criada.
     */
    @PostMapping
    @Operation(summary = "Criar nota", description = "Cria uma nova nota para o usuário logado.")
    public Note createNote(@RequestBody Note note, Authentication authentication) {
        String username = authentication.getName(); // Obtém o nome de usuário do login
        User user = userRepository.findByUsername(username)
                                   .orElseThrow(() -> new RuntimeException("User not found"));
        note.setUser(user);
        note.setUpdatedAt(new Date());
        return noteRepository.save(note);
    }

    /**
     * Buscar notas por conteúdo.
     *
     * @param content Texto para pesquisa.
     * @param authentication Dados do usuário autenticado.
     * @return Lista de notas contendo o texto.
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar notas", description = "Busca notas que contêm o texto especificado.")
    public List<Note> searchNotes(@RequestParam String content, Authentication authentication) {
        String username = authentication.getName(); // Obtém o nome do usuário logado
        User user = userRepository.findByUsername(username)
                                   .orElseThrow(() -> new RuntimeException("User not found"));
        return noteRepository.findByUserAndContentContaining(user, content);
    }

    /**
     * Atualizar uma nota existente.
     *
     * @param id ID da nota a ser atualizada.
     * @param note Dados da nota atualizados.
     * @param authentication Dados do usuário autenticado.
     * @return A nota atualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nota", description = "Atualiza uma nota existente do usuário logado.")
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

    /**
     * Deletar uma nota existente.
     *
     * @param id ID da nota a ser deletada.
     * @param authentication Dados do usuário autenticado.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar nota", description = "Deleta uma nota existente do usuário logado.")
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
