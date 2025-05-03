package com.example.todoapp.user.controller;

import com.example.todoapp.user.User;
import com.example.todoapp.user.service.UserMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {

    @Test
    void fromDTOTest() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setPassword("password");

        User user = UserMapper.fromDTO(userDTO);

        assertEquals(userDTO.getUsername(), user.getUsername());
        assertEquals(userDTO.getPassword(), user.getPassword());
        assertNull(user.getId());
    }

    @Test
    void toDTO() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setId(1L);

        UserDTO userDTO = UserMapper.toDTO(user);

        assertEquals(user.getUsername(), userDTO.getUsername());
        assertNull(userDTO.getPassword());
    }

}
