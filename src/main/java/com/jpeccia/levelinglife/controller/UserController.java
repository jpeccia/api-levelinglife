package com.jpeccia.levelinglife.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpeccia.levelinglife.dto.LoginRequestDTO;
import com.jpeccia.levelinglife.dto.ResponseDTO;
import com.jpeccia.levelinglife.dto.UserRegisterDTO;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.infra.security.TokenService;
import com.jpeccia.levelinglife.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        User user = repository.findByUsername(body.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getUsername(), token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO body) {
        Optional<User> existingUser = repository.findByEmail(body.getEmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already registered");
        }

        // Criar o novo usuário
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(body.getPassword()));
        newUser.setEmail(body.getEmail());
        newUser.setUsername(body.getUsername());
        newUser.setName(body.getName());
        
        repository.save(newUser);

        String token = tokenService.generateToken(newUser);
        return ResponseEntity.ok(new ResponseDTO(newUser.getUsername(), token));
    }
}
