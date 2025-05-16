package com.example.todoapp.task.repository;

import com.example.todoapp.task.Task;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TaskRowMapper implements RowMapper<Task> {
    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));
        task.setDeadline(rs.getDate("deadline").toLocalDate());
        task.setPriority(rs.getInt("priority"));
        task.setCompleted(rs.getBoolean("completed"));
        task.setUserId(rs.getLong("user_id"));
        task.setDescription(rs.getString("description"));

        task.setPublicId(UUID.fromString(rs.getString("public_id")));
        return task;
    }
}
