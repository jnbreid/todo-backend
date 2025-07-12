/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.user.controller;

import com.example.todoapp.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User fromDTO(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }
}
