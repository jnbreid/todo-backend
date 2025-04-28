package com.example.todoapp.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

}
