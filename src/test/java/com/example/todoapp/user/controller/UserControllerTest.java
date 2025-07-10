/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.user.controller;

import com.example.todoapp.config.RestTestConfig;
import com.example.todoapp.config.security.CustomUserDetailsService;
import com.example.todoapp.config.security.JwtTokenUtil;
import com.example.todoapp.config.security.SecurityConfig;
import com.example.todoapp.user.User;
import com.example.todoapp.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(RestTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String username = "username";
    private final String password = "password";


    @Test
    void loginTest_Success() throws Exception{
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userService.authenticate(userDTO.getUsername(), userDTO.getPassword()))
                .thenReturn(user);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginTest_Failure() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("wrongPassword");

        when(userService.authenticate(userDTO.getUsername(), userDTO.getPassword()))
                .thenThrow(new BadCredentialsException("Invalid password or username."));

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_InvalidInputs() throws Exception {
        String invalidUsername = "a".repeat(62);
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(invalidUsername);
        userDTO.setPassword("password");

        doThrow(new IllegalArgumentException("Username to long. 60 characters max."))
                .when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("Username to long. 60 characters max."));

        verify(userService).registerUser(any(User.class));
    }

    @Test
    void register_OtherException() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setPassword("password");

        doThrow(new RuntimeException())
                .when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred."));
    }

    @Test
    void registerUserTest() throws Exception{
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setPassword("password");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        verify(userService).registerUser(any(User.class));

    }
}
