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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
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

    @Test
    void findUserIdByUserName_Success() {
        User user = new User();
        user.setUsername("UserName");
        user.setPassword("hashedPassword");
        user.setId(1L);

        //mock user found
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //
        Long userId = userService.findUserIdByUserName(user.getUsername());
        assertEquals(user.getId(), userId);
    }

    @Test
    void findUserIdByUserName_Failure() {
        User user = new User();
        user.setUsername("UserName");
        user.setPassword("hashedPassword");
        user.setId(1L);

        //mock user found
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        //
        assertThrows(IllegalArgumentException.class, () -> userService.findUserIdByUserName(user.getUsername()));
    }

    @Test
    void findUserNameByUserId_Success() {
        User user = new User();
        user.setUsername("UserName");
        user.setPassword("hashedPassword");
        user.setId(1L);

        //mock user found
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //
        String name = userService.findUserNameByUserId(user.getId());
        assertEquals(user.getUsername(), name);
    }

    @Test
    void findUserNameByUserId_Failure() {
        User user = new User();
        user.setUsername("UserName");
        user.setPassword("hashedPassword");
        user.setId(1L);

        //mock user found
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        //
        assertThrows(IllegalArgumentException.class, () -> userService.findUserNameByUserId(user.getId()));
    }


    @Test
    void delteSelf_success() {
        // initialize security context

        // 1. Create an empty context
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();

        // 2. Build an Authentication (username, credentials, authorities)
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "UserName",
                "ignored-password",
                List.of()
        );
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        // initialize other objects needed for tests

        User userIncoming = new User();
        userIncoming.setUsername("UserName");
        userIncoming.setPassword("rawPassword");
        userIncoming.setId(1L);

        User userStored = new User();
        userStored.setUsername("UserName");
        userStored.setPassword("hashedPassword");
        userStored.setId(1L);

        when(userRepository.findByUsername(userIncoming.getUsername())).thenReturn(Optional.of(userStored));
        when(passwordEncoder.matches("rawPassword", "hashedPassword")).thenReturn(true);

        userService.deleteSelf(userIncoming);

        verify(userRepository).delete("UserName");
    }

    @Test
    void deleteSelf_failure_incorrectPassword() {
        User userIncoming = new User();
        userIncoming.setUsername("UserName");
        userIncoming.setPassword("rawPassword");
        userIncoming.setId(1L);

        when(userRepository.findByUsername(userIncoming.getUsername())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> userService.deleteSelf(userIncoming));

        verify(userRepository, never()).delete(anyString());
    }

    @Test
    void delteSelf_failure_notCurrentUser() {
        // initialize security context

        // 1. Create an empty context
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();

        // 2. Build an Authentication (username, credentials, authorities)
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "OtherUsername",   // <-- username is different
                "ignored-password",
                List.of()
        );
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        // initialize other objects needed for tests

        User userIncoming = new User();
        userIncoming.setUsername("UserName");
        userIncoming.setPassword("rawPassword");
        userIncoming.setId(1L);

        User userStored = new User();
        userStored.setUsername("UserName");
        userStored.setPassword("hashedPassword");
        userStored.setId(1L);

        when(userRepository.findByUsername(userIncoming.getUsername())).thenReturn(Optional.of(userStored));
        when(passwordEncoder.matches("rawPassword", "hashedPassword")).thenReturn(true);

        assertThrows(AccessDeniedException.class, () -> userService.deleteSelf(userIncoming));

        // verify that delete is never called
        verify(userRepository, never()).delete(anyString());
    }

}


