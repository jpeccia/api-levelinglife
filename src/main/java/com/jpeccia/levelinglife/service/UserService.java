package com.jpeccia.levelinglife.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User updateUser(User user){
        return userRepository.save(user);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUserProfile(String username, String newName) {
        User user = getUserByUsername(username);
        user.setName(newName);
        return userRepository.save(user);
    }

}
