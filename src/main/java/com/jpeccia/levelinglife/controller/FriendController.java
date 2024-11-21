package com.jpeccia.levelinglife.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.dto.UserProfileDTO;
import com.jpeccia.levelinglife.entity.FriendRequest;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.service.FriendRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/friends")
public class FriendController {
    @Autowired
    private FriendRequestService friendRequestService;

    // Envia um pedido de amizade usando o username
    @Operation(summary = "Envio de pedido de amizade", description = "Envia um pedido de amizade para outro usuário.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Pedido de amizade enviado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Erro ao enviar pedido de amizade")
    })
    @PostMapping("/add/{username}")
    public ResponseEntity<String> sendFriendRequest(@Parameter(description = "Nome de usuário do amigo")@PathVariable String username) {
        
        User sender = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = friendRequestService.sendFriendRequest(sender, username);
        return ResponseEntity.ok(result);
    }

    // Aceita ou recusa um pedido de amizade
    @Operation(summary = "Responder a um pedido de amizade", description = "Aceita ou recusa um pedido de amizade de outro usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido de amizade respondido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido de amizade não encontrado"),
            @ApiResponse(responseCode = "403", description = "Você não tem permissão para responder a este pedido")
    })
    @PostMapping("/respond/{requestId}")
    public ResponseEntity<String> respondToFriendRequest(@Parameter(description = "ID do pedido de amizade")@PathVariable Long requestId, 
    @Parameter(description = "Aceitar ou recusar o pedido de amizade")@RequestParam boolean accept) {
        // Obter o usuário autenticado
        User receiver = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Passar o usuário autenticado como destinatário ao serviço
        String result = friendRequestService.respondToFriendRequest(receiver, requestId, accept);
        return ResponseEntity.ok(result);
    }

    // Lista os pedidos de amizade pendentes
    @Operation(summary = "Listar pedidos de amizade pendentes", description = "Lista todos os pedidos de amizade que aguardam resposta.")
    @ApiResponse(responseCode = "200", description = "Pedidos de amizade pendentes encontrados")
    @GetMapping("/pending-friend-requests")
    public ResponseEntity<List<FriendRequest>> getPendingFriendRequests() {
        User receiver = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<FriendRequest> requests = friendRequestService.getPendingFriendRequests(receiver);
        return ResponseEntity.ok(requests);
    }

    // Lista amigos (usuários com pedidos aceitos)
    @Operation(summary = "Listar amigos", description = "Lista todos os amigos (usuários com pedidos de amizade aceitos).")
    @ApiResponse(responseCode = "200", description = "Lista de amigos encontrada")
    @GetMapping("/")
    public ResponseEntity<List<UserProfileDTO>> getFriends() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
        // Busca todas as solicitações de amizade (ou pode buscar apenas as aceitas)
        List<FriendRequest> acceptedRequests = friendRequestService.getAcceptedFriendRequests(user);
    
        // Filtra os amigos com status ACCEPTED
        List<UserProfileDTO> friends = acceptedRequests.stream()
                .filter(request -> request.getStatus() == FriendRequest.Status.ACCEPTED)
                .map(request -> {
                    // Verifica qual usuário é o amigo, pois pode ser o sender ou receiver
                    User friend = request.getSender().equals(user) ? request.getReceiver() : request.getSender();
                    return new UserProfileDTO(friend.getName(), friend.getUsername(), friend.getTitle(), friend.getEmail(), friend.getLevel(), friend.getXp(), friend.getProfilePicture());
                })
                .collect(Collectors.toList());
    
        return ResponseEntity.ok(friends);
    }

    // Remove um amigo
    @Operation(summary = "Remover um amigo", description = "Remove um amigo, cancelando a amizade com base no nome de usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Amigo removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Amigo não encontrado")
    })
    @DeleteMapping("/remove/{friendUsername}")
    public ResponseEntity<String> removeFriend(@PathVariable String friendUsername) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = friendRequestService.removeFriend(user, friendUsername);
        return ResponseEntity.ok(result);
    }
}