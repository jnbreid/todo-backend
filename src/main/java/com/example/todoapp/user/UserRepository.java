package com.example.todoapp.user;

import org.springframework.jdbc.core.ResultSetExtractor;
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
    ResultSetExtractor<User> resultSetExtractor = new UserResultSetExtractor();

    public int create(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        return client.sql(sql).params(user.getUsername(), user.getPassword()).update();
    }

    public List<User> findAll() {
        String sql = "SELECT id, username, password FROM users";
        return client.sql(sql).query(rowMapper).list();
    }

    public User getItem(String username) {
        String sql = "SELECT FROM users WHERE username = ?";
        return client.sql(sql).params(username).query(resultSetExtractor);
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
