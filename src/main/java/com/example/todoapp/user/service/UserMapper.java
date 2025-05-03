package com.example.todoapp.user.service;

import com.example.todoapp.user.User;
import com.example.todoapp.user.controller.UserDTO;

public class UserMapper {

    public static User fromDTO(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }
}
