package com.jpeccia.levelinglife.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.jpeccia.levelinglife.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByLogin(String login);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);

    
}
