package com.jpeccia.levelinglife.repository;

import com.jpeccia.levelinglife.entity.FriendRequest;
import com.jpeccia.levelinglife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.Status status);

    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);

    // Busca um usuário pelo username
    Optional<User> findByUsername(String username);

}