/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.user.repository;

import com.example.todoapp.user.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserRepository {
    private final JdbcClient client;

    public UserRepository(JdbcClient client) {
        this.client = client;
    }

    RowMapper<User> rowMapper = new UserRowMapper();

    public int create(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        return client.sql(sql).params(user.getUsername(), user.getPassword()).update();
    }

    public List<User> findAll() {
        String sql = "SELECT id, username, password FROM users";
        return client.sql(sql).query(rowMapper).list();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        return client.sql(sql).params(username).query(rowMapper).optional();
    }

    public Optional<User> findById(Long userId) {
        String sql = "SELECT id, username, password FROM users WHERE id = ?";
        return client.sql(sql).params(userId).query(rowMapper).optional();
    }

    public int delete(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        return client.sql(sql).params(username).update();
    }

    public int delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return client.sql(sql).params(id).update();
    }

}
