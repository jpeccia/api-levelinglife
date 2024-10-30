package com.jpeccia.levelinglife.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpeccia.levelinglife.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User findByUsernameOrEmail(String username, String email);

    
}
