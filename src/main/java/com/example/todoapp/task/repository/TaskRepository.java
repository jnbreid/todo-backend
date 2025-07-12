/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.task.repository;

import com.example.todoapp.task.Task;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class TaskRepository {
    private final JdbcClient client;
    private final TaskRowMapper rowMapper;

    public TaskRepository(JdbcClient client, TaskRowMapper taskRowMapper) {
        this.client = client;
        this.rowMapper = taskRowMapper;
    }


    public int create(Task task) {
        String sql = "INSERT INTO tasks (name, description, deadline, priority, completed, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        return client.sql(sql).params(task.getName(), task.getDescription(), task.getDeadline(), task.getPriority(), task.getCompleted(), task.getUserId()).update();
    }

    public List<Task> findSet(long userId) {
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY deadline DESC";
        return client.sql(sql).params(userId).query(rowMapper).list();
    }

    public Optional<Task> findById(long taskId) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        return client.sql(sql).params(taskId).query(rowMapper).optional();
    }

    public Optional<Task> findByPublicId(UUID publicId) {
        String sql = "SELECT * FROM tasks WHERE public_id = ?";
        return client.sql(sql).params(publicId).query(rowMapper).optional();
    }

    public int update(Task task, long taskId) {
        String sql = "UPDATE tasks SET name = ?, description = ?, deadline = ?, priority = ?, completed = ?, user_id = ? WHERE id = ?";
        return client.sql(sql).params(
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriority(),
                task.getCompleted(),
                task.getUserId(),
                taskId
        ).update();
    }

    public int update(Task task, UUID publicTaskId) {
        String sql = "UPDATE tasks SET name = ?, description = ?, deadline = ?, priority = ?, completed = ?, user_id = ? WHERE public_id = ?";
        return client.sql(sql).params(
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriority(),
                task.getCompleted(),
                task.getUserId(),
                publicTaskId
        ).update();
    }

    public int delete(long taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        return client.sql(sql).params(taskId).update();
    }

    public int delete(UUID publicTaskId) {
        String sql = "DELETE FROM tasks WHERE public_id = ?";
        return client.sql(sql).params(publicTaskId).update();
    }

}
