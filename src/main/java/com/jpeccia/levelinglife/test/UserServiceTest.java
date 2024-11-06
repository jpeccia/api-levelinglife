package com.jpeccia.levelinglife.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jpeccia.levelinglife.entity.User;
import com.jpeccia.levelinglife.repository.UserRepository;
import com.jpeccia.levelinglife.service.UserService;

public class UserServiceTest {
    

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setUsername("newuser");
        mockUser.setEmail("newuser@example.com");
        mockUser.setPassword("password123");
    }

    @Test
    public void testCreateUser() {
        // Arrange
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        // Act
        User createdUser = userService.createUser(mockUser);

        // Assert
        assertNotNull(createdUser);
        assertEquals("john_doe", createdUser.getUsername());
        assertEquals("John Doe", createdUser.getName());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    public void testGetUserByUsername() {
        // Arrange
        when(userRepository.findByUsername("john_doe")).thenReturn(java.util.Optional.of(mockUser));

        // Act
        User foundUser = userService.getUserByUsername("john_doe");

        // Assert
        assertNotNull(foundUser);
        assertEquals("john_doe", foundUser.getUsername());
        assertEquals("John Doe", foundUser.getName());
        verify(userRepository, times(1)).findByUsername("john_doe");
    }

    @Test
    public void testUpdateUserProfile() {
        // Arrange
        when(userRepository.findByUsername("john_doe")).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        // Act
        User updatedUser = userService.updateUserProfile("john_doe", "Johnathan Doe");

        // Assert
        assertNotNull(updatedUser);
        assertEquals("Johnathan Doe", updatedUser.getName());
        verify(userRepository, times(1)).findByUsername("john_doe");
        verify(userRepository, times(1)).save(mockUser);
    }
}
