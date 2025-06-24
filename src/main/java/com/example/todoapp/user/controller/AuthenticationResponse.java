/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.user.controller;

public class AuthenticationResponse {
    private String token;
    private String username;

    public AuthenticationResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}