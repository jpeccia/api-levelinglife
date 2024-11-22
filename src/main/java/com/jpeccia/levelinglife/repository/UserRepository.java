package com.jpeccia.levelinglife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpeccia.levelinglife.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User findByUsernameOrEmail(String username, String email);
    // Método para obter os 10 usuários com maior nível
    List<User> findTop10ByOrderByLevelDesc();
    User findByUsernameAndNote(String username);

    
}
