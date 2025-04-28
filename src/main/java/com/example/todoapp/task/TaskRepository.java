package com.example.todoapp.task;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TaskRepository {
    private final JdbcClient client;

    public TaskRepository(JdbcClient client) {
        this.client = client;
    }

    public int create(Task task) {
        String sql = "INSERT INTO tasks (name, deadline, priority, completed, user_id) VALUES (?, ?, ?, ?, ?)";
        return client.sql(sql).params(task.getName(), task.getDeadline(), task.getPriority(), task.getCompleted(), task.getUserId()).update();
    }


}
