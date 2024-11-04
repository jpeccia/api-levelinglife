package com.jpeccia.levelinglife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpeccia.levelinglife.entity.Friendship;
import com.jpeccia.levelinglife.entity.FriendshipStatus;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.FriendshipRepository;
import com.jpeccia.levelinglife.repository.UserRepository;

@Service
public class FriendshipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    // Método para enviar uma solicitação de amizade
    public Friendship sendFriendRequest(Long requesterId, Long friendId) {
        User requester = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("Requester not found"));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = new Friendship();
        friendship.setRequester(requester);
        friendship.setFriend(friend);

        return friendshipRepository.save(friendship);
    }

    // Método para aceitar uma solicitação de amizade
    public Friendship acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId).orElseThrow(() -> new RuntimeException("Friendship not found"));
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    // Método para rejeitar uma solicitação de amizade
    public Friendship rejectFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId).orElseThrow(() -> new RuntimeException("Friendship not found"));
        friendship.setStatus(FriendshipStatus.REJECTED);
        return friendshipRepository.save(friendship);
    }

    // Método para listar amigos de um usuário
    public List<Friendship> listFriends(Long userId) {
        return friendshipRepository.findAllByRequesterIdAndStatusOrFriendIdAndStatus(userId, FriendshipStatus.ACCEPTED, userId, FriendshipStatus.ACCEPTED);
    }
}
