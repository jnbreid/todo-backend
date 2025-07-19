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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
        type  = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
))
@Import(RestTestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserMapper userMapper;

    @Test
    void loginTest_Success() throws Exception{
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password", List.of());
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername("testuser")
                    .password("password")
                    .roles("USER")
                    .build();
        when(customUserDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);


        // fake token 'generated one' from jwt util
        String fakeToken = "fake-jwt-token";
        when(jwtTokenUtil.generateToken(userDetails))
                .thenReturn(fakeToken);


        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeToken))
                .andExpect(jsonPath("$.username").value("testuser"));

    }

    @Test
    void loginTest_Failure() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("wrongPassword");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized());

        verify(customUserDetailsService, never()).loadUserByUsername(any());
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    void registerUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setPassword("securePass");

        User user = new User();
        user.setUsername("newuser");
        user.setPassword("securePass");

        when(userMapper.fromDTO(userDTO)).thenReturn(user);
        doNothing().when(userService).registerUser(user);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_Failure_InvalidUsername() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("a".repeat(100));
        userDTO.setPassword("password");

        User dummyUser = new User();
        when(userMapper.fromDTO(any(UserDTO.class))).thenReturn(dummyUser);

        doThrow(new IllegalArgumentException("Username to long. 60 characters max."))
                .when(userService).registerUser(dummyUser);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username to long. 60 characters max."));
    }

    @Test
    void registerUser_Failure_ExistingUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("username");
        userDTO.setPassword("password");

        User dummy = new User();

        when(userMapper.fromDTO(any(UserDTO.class))).thenReturn(dummy);

        doThrow(new IllegalArgumentException("Creating user failed."))
                .when(userService).registerUser(dummy);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Creating user failed."));

    }

    @Test
    void deleteSelf_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("user");
        userDTO.setPassword("password");

        User user = new User();
        user.setUsername("user");
        user.setPassword("password");

        when(userMapper.fromDTO(userDTO)).thenReturn(user);
        doNothing().when(userService).deleteSelf(user);

        mockMvc.perform(delete("/api/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNoContent());
    }


}