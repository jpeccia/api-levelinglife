package com.jpeccia.levelinglife.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpeccia.levelinglife.entity.Friendship;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.FriendshipRepository;
import com.jpeccia.levelinglife.repository.UserRepository;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    // Adiciona um amigo para o usuário atual
    public String addFriend(User user, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        // Verifica se já existe uma amizade entre os usuários
        if (friendshipRepository.findByUserAndFriend(user, friend).isPresent()) {
            return "Você já é amigo desse usuário.";
        }

        // Salva a amizade no banco de dados
        friendshipRepository.save(new Friendship(user, friend));
        friendshipRepository.save(new Friendship(friend, user)); // Amizade bidirecional

        return "Amizade adicionada com sucesso.";
    }

    // Retorna a lista de amigos de um usuário
    public List<User> getFriends(User user) {
        List<Friendship> friendships = friendshipRepository.findByUser(user);
        return friendships.stream()
                .map(Friendship::getFriend)
                .collect(Collectors.toList());
    }
}