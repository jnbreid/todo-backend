package com.example.todoapp.task;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TaskRepository {
    private final JdbcClient client;

    public TaskRepository(JdbcClient client) {
        this.client = client;
    }

    RowMapper<Task> rowMapper = new TaskRowMapper();
    ResultSetExtractor<Task> resultSetExtractor = new TaskResultSetExtractor();

    public int create(Task task) {
        String sql = "INSERT INTO tasks (name, deadline, priority, completed, user_id) VALUES (?, ?, ?, ?, ?)";
        return client.sql(sql).params(task.getName(), task.getDeadline(), task.getPriority(), task.getCompleted(), task.getUserId()).update();
    }

    public List<Task> findSet(int userId) {
        String sql = "SELECT id, name, deadline, priority, completed, user_id FROM users WHERE user_id = ?";
        return client.sql(sql).params(userId).query(rowMapper).list();
    }

    public Task getItem(int taskId) {
        String sql = "SELECT \"SELECT id, name, deadline, priority, completed, user_id FROM users WHERE id = ?";
        return client.sql(sql).params(taskId).query(resultSetExtractor);
    }

    public int update(Task task, int taskId) {
        String sql = "UPDATE tasks SET name = ?, deadline = ? priority = ? completed = ? user_id = ? WHERE id = ?";
        return client.sql(sql).params(
                task.getName(),
                task.getDeadline(),
                task.getPriority(),
                task.getCompleted(),
                task.getUserId()
        ).update();
    }

    public int delete(int taskId) {
        String sql = "DELETE FROM tasks WHERE is = ?";
        return client.sql(sql).params(taskId).update();
    }




}
