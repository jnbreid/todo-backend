/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.user.controller;

import com.example.todoapp.config.security.CustomUserDetailsService;
import com.example.todoapp.config.security.JwtTokenUtil;
import com.example.todoapp.user.User;
import com.example.todoapp.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserMapper userMapper;


    public UserController(UserService userService, AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.fromDTO(userDTO);
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserDTO request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthenticationResponse(token, request.getUsername()));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSelf(@RequestBody UserDTO request) {
        User user = userMapper.fromDTO(request);
        userService.deleteSelf(user);
        return ResponseEntity.noContent().build();
    }

}
