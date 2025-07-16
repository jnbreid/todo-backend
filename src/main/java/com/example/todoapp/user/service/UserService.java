/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.user.service;

import com.example.todoapp.user.User;
import com.example.todoapp.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void validateUser(User user) {
        if(user.getUsername().length() > 60) {
            throw new IllegalArgumentException("Username to long. 60 characters max.");
        }
    }

    public void registerUser(User user) {
        validateUser(user);
        userRepository.findByUsername(user.getUsername()).ifPresent( variable -> {
            throw new IllegalArgumentException("Creating user failed.");
        });

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.create(user);
    }

    public User authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username).filter(
                user -> passwordEncoder.matches(rawPassword, user.getPassword())).orElseThrow(
                        () -> new BadCredentialsException("Invalid password or username."));
    }


    public Long findUserIdByUserName(String userName) {
        return userRepository.findByUsername(userName).map(User::getId).orElseThrow(
                () -> new IllegalArgumentException("User not found.")
        );
    }

    public String findUserNameByUserId(Long userId) {
        return userRepository.findById(userId).map(User::getUsername).orElseThrow(
                () -> new IllegalArgumentException("User not found.")
        );
    }

    public void deleteSelf(User delUser) {
        String username = delUser.getUsername();
        String rawPassword = delUser.getPassword();

        userRepository.findByUsername(username).filter(
                user -> passwordEncoder.matches(rawPassword, user.getPassword())).orElseThrow(
                () -> new BadCredentialsException("Invalid password or username.")
        );

        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authUsername.equals(username)) {
            throw new AccessDeniedException("You can only delete your own account.");
        }

        userRepository.delete(username);
    }
}
