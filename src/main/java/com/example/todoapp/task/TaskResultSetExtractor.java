package com.example.todoapp.task;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskResultSetExtractor implements ResultSetExtractor<Task> {

    @Override
    public Task extractData(ResultSet rs) throws SQLException, DataAccessException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));
        task.setDeadline(rs.getDate("deadline").toLocalDate());
        task.setPriority(rs.getInt("priority"));
        task.setCompleted(rs.getBoolean("completed"));
        task.setUserId(rs.getLong("user_id"));
        return task;
    }
}
