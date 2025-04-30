package com.example.todoapp.task;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TaskRepository {
    private final JdbcClient client;

    public TaskRepository(JdbcClient client) {
        this.client = client;
    }

    RowMapper<Task> rowMapper = new TaskRowMapper();

    public int create(Task task) {
        String sql = "INSERT INTO tasks (name, deadline, priority, completed, user_id) VALUES (?, ?, ?, ?, ?)";
        return client.sql(sql).params(task.getName(), task.getDeadline(), task.getPriority(), task.getCompleted(), task.getUserId()).update();
    }

    public List<Task> findSet(long userId) {
        String sql = "SELECT id, name, deadline, priority, completed, user_id FROM tasks WHERE user_id = ?";
        return client.sql(sql).params(userId).query(rowMapper).list();
    }

    public Optional<Task> findById(long taskId) {
        String sql = "SELECT id, name, deadline, priority, completed, user_id FROM tasks WHERE id = ?";
        return client.sql(sql).params(taskId).query(rowMapper).optional();
    }

    public int update(Task task, long taskId) {
        String sql = "UPDATE tasks SET name = ?, deadline = ?, priority = ?, completed = ?, user_id = ? WHERE id = ?";
        return client.sql(sql).params(
                task.getName(),
                task.getDeadline(),
                task.getPriority(),
                task.getCompleted(),
                task.getUserId(),
                taskId
        ).update();
    }

    public int delete(long taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        return client.sql(sql).params(taskId).update();
    }




}
