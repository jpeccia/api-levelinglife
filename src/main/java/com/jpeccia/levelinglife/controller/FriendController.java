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

@RestController
@RequestMapping("/friends")
public class FriendController {
    @Autowired
    private FriendRequestService friendRequestService;

    // Envia um pedido de amizade usando o username
    @PostMapping("/add/{username}")
    public ResponseEntity<String> sendFriendRequest(@PathVariable String username) {
        User sender = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = friendRequestService.sendFriendRequest(sender, username);
        return ResponseEntity.ok(result);
    }

    // Aceita ou recusa um pedido de amizade
    @PostMapping("/respond/{requestId}")
    public ResponseEntity<String> respondToFriendRequest(@PathVariable Long requestId, @RequestParam boolean accept) {
        // Obter o usuário autenticado
        User receiver = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Passar o usuário autenticado como destinatário ao serviço
        String result = friendRequestService.respondToFriendRequest(receiver, requestId, accept);
        return ResponseEntity.ok(result);
    }

    // Lista os pedidos de amizade pendentes
    @GetMapping("/pending-friend-requests")
    public ResponseEntity<List<FriendRequest>> getPendingFriendRequests() {
        User receiver = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<FriendRequest> requests = friendRequestService.getPendingFriendRequests(receiver);
        return ResponseEntity.ok(requests);
    }

    // Lista amigos (usuários com pedidos aceitos)
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
                    return new UserProfileDTO(friend.getName(), friend.getLevel(), friend.getXp(), friend.getProfilePicture());
                })
                .collect(Collectors.toList());
    
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/remove/{friendUsername}")
    public ResponseEntity<String> removeFriend(@PathVariable String friendUsername) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = friendRequestService.removeFriend(user, friendUsername);
        return ResponseEntity.ok(result);
    }
}