/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.user.service;

import com.example.todoapp.user.User;
import com.example.todoapp.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUserTest_Success() {
        User user = new User();
        user.setUsername("TestName");
        user.setPassword("TestPswd");

        // no existing user
        when(userRepository.findByUsername("TestName")).thenReturn(Optional.empty());
        // password encoding
        when(passwordEncoder.encode("TestPswd")).thenReturn("hashedPassword");

        // execute
        userService.registerUser(user);

        // assert
        // that create is called in UserRepository
        verify(userRepository).create(user);
        // password encoded correctly
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    void registerUserTest_Failure() {
        // setup
        User existingUser = new User();
        existingUser.setUsername("existingUsername");
        existingUser.setPassword("empty");

        // mock user existing in database
        when(userRepository.findByUsername("existingUsername")).thenReturn(Optional.of(existingUser));

        User newUser = new User();
        newUser.setUsername("existingUsername");
        newUser.setPassword("newPassword");

        // execute + assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(newUser)
        );

        assertEquals("Creating user failed.", exception.getMessage());
        // ensure create() not called
        verify(userRepository, never()).create(any());
    }

    @Test
    void authenticateTest_Success() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("hashedPassword");

        // mock existing user in database
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password1234", user.getPassword())).thenReturn(true);

        // execute
        User resultUser = userService.authenticate("username", "password1234");

        // Assert
        assertNotNull(resultUser);
        assertEquals("username", resultUser.getUsername());
    }

    @Test
    void authenticateTest_Failure_InvalidPassword() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("hashedPassword");

        // mock user in database
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        // mock false password
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        // execute + assert
        assertThrows(BadCredentialsException.class, () -> userService.authenticate(user.getUsername(), "wrongPassword"));
    }

    @Test
    void authenticateTest_Failure_InvalidUsername() {
        User user = new User();
        user.setUsername("notExisting");
        user.setPassword("hashedPassword");

        // mock no user found
        when(userRepository.findByUsername("notExisting")).thenReturn(Optional.empty());

        // execute + assert
        assertThrows(BadCredentialsException.class, () -> userService.authenticate(user.getUsername(), user.getPassword()));
    }

}


