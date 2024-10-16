package com.jpeccia.levelinglife.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jpeccia.levelinglife.dto.UserRegisterDTO;
import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.infra.security.TokenService;
import com.jpeccia.levelinglife.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;
    

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) throws Exception{

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new Exception("E-mail já cadastrado");
        }

        Optional<User> existingUsername = userRepository.findByUsername(user.getUsername());
        if (existingUsername.isPresent()) {
            throw new Exception("Nome de usuário já cadastrado");
        }       

        User newUser = new User();
        newUser.setUsername(newUser.getUsername());
        newUser.setEmail(newUser.getEmail());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userRepository.save(newUser);
    }

    public User updateUser(User user){
        return userRepository.save(user);
    }

    public User loginUser(String usernameOrEmail, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (!userOptional.isPresent()) {
            throw new Exception("Usuário não encontrado");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Senha incorreta");
        }

        return user; 
    }

}
