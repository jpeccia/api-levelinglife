package com.jpeccia.levelinglife.controller;

import com.jpeccia.levelinglife.dto.NoteRequestDTO;
import com.jpeccia.levelinglife.dto.NoteResponseDTO;
import com.jpeccia.levelinglife.entity.Note;
import com.jpeccia.levelinglife.repository.NoteRepository;
import com.jpeccia.levelinglife.service.NoteService;
import com.jpeccia.levelinglife.service.NoteService.PermissionDeniedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteRepository noteRepository;



    @Operation(summary = "Obter todas as notas do usuário", description = "Retorna todas as notas associadas ao usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notas recuperadas com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getNotes() throws Exception {
        // Recupera as notas do serviço já convertidas para o DTO
        List<NoteResponseDTO> response = noteService.getNotes();

        // Retorna a resposta com o status OK
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Criar uma nova nota", description = "Cria uma nova nota associada ao usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Nota criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos para criação da nota")
    })
    @Transactional
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody NoteRequestDTO noteDto) {
        try {
            Note createdNote = noteService.createNote(noteDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
        } catch (IllegalArgumentException e) {
            // Tratar exceção de dados inválidos (por exemplo, DTO malformado)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            // Tratar outras exceções genéricas
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Atualizar uma nota existente", description = "Atualiza uma nota existente associada ao usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nota atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody NoteRequestDTO noteDto) {
        try {
            Note updatedNote = noteService.updateNote(id, noteDto);
            return ResponseEntity.ok(updatedNote);
        } catch (NoteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Nota não encontrada
        } catch (PermissionDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  // Permissão negada
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Erro interno do servidor
        }
    }

    @Operation(summary = "Deletar uma nota", description = "Deleta uma nota associada ao usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Nota deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        try {
            noteService.deleteNote(id);
            return ResponseEntity.noContent().build();
        } catch (NoteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Retorna 404 se não encontrar
        } catch (DataIntegrityViolationException e) {
            // Caso a exclusão falhe devido a uma violação de integridade (ex: chave estrangeira)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  // Retorna 409
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // Retorna 500
        }
    }

    public class NoteNotFoundException extends RuntimeException {
        public NoteNotFoundException(String message) {
            super(message);
        }
    }
    

}
