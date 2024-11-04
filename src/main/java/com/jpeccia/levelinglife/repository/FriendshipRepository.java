package com.jpeccia.levelinglife.repository;

import com.jpeccia.levelinglife.entity.Friendship;
import com.jpeccia.levelinglife.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Retorna uma lista de amigos de um usuário
    List<Friendship> findByUser(User user);

    // Verifica se uma amizade já existe entre dois usuários
    Optional<Friendship> findByUserAndFriend(User user, User friend);
}