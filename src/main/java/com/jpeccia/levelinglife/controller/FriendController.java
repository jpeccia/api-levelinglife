package com.jpeccia.levelinglife.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.dto.UserProfileDTO;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;
import com.jpeccia.levelinglife.service.FriendshipService;

@RestController
@RequestMapping("/friends")
public class FriendController {
    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserRepository userRepository;

    // Endpoint para adicionar um amigo
    @PostMapping("/add/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Long friendId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = friendshipService.addFriend(user, friendId);
        return ResponseEntity.ok(result);
    }

    // Endpoint para listar os amigos do usuário autenticado
    @GetMapping("/")
    public ResponseEntity<List<UserProfileDTO>> getFriends() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> friends = friendshipService.getFriends(user);

        // Converter para UserProfileDTO para limitar os dados
        List<UserProfileDTO> friendProfiles = friends.stream()
                .map(friend -> new UserProfileDTO(friend.getName(), friend.getLevel(), friend.getXp(), friend.getProfilePicture()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(friendProfiles);
    }

    // Endpoint para visualizar o perfil de um amigo
    @GetMapping("/friend-profile/{friendId}")
    public ResponseEntity<UserProfileDTO> getFriendProfile(@PathVariable Long friendId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> friends = friendshipService.getFriends(user);

        // Verifica se o usuário consultado está na lista de amigos
        User friend = friends.stream()
                .filter(f -> f.getId().equals(friendId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Esse usuário não é seu amigo."));

        // Retorna o perfil do amigo
        UserProfileDTO profile = new UserProfileDTO(friend.getName(), friend.getLevel(), friend.getXp(), friend.getProfilePicture());
        return ResponseEntity.ok(profile);
    }
}