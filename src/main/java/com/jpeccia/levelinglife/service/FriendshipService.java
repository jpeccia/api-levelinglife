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

    // Envia um pedido de amizade
    public String sendFriendRequest(User user, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        if (friendshipRepository.findByUserAndFriend(user, friend).isPresent()) {
            return "Você já é amigo desse usuário.";
        }

        Friendship friendship = new Friendship(user, friend);
        friendshipRepository.save(friendship);

        return "Pedido de amizade enviado com sucesso.";
    }

    // Aceita um pedido de amizade
    public String acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Pedido de amizade não encontrado."));

        friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        // Adiciona a amizade bidirecional
        Friendship reverseFriendship = new Friendship(friendship.getFriend(), friendship.getUser());
        reverseFriendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        friendshipRepository.save(reverseFriendship);

        return "Pedido de amizade aceito.";
    }

    // Rejeita um pedido de amizade
    public String rejectFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Pedido de amizade não encontrado."));

        friendship.setStatus(Friendship.FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);

        return "Pedido de amizade rejeitado.";
    }

    // Remove um amigo
    public String removeFriend(User user, Long friendId) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Amigo não encontrado."));

        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend)
                .orElseThrow(() -> new RuntimeException("Amizade não encontrada."));

        friendshipRepository.delete(friendship); // Remove a amizade
        friendshipRepository.delete(friendshipRepository.findByUserAndFriend(friend, user).orElseThrow(() -> new RuntimeException("Amizade não encontrada."))); // Remove a amizade inversa

        return "Amigo removido com sucesso.";
    }

    // Retorna a lista de amigos de um usuário
    public List<User> getFriends(User user) {
        List<Friendship> friendships = friendshipRepository.findByUserAndStatus(user, Friendship.FriendshipStatus.ACCEPTED);
        return friendships.stream()
                .map(Friendship::getFriend)
                .collect(Collectors.toList());
    }
}