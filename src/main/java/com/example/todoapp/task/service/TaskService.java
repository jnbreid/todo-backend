/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.task.service;

import com.example.todoapp.task.Task;
import com.example.todoapp.task.repository.TaskRepository;
import com.example.todoapp.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    private static final int MIN_PRIORITY = 1;
    private static final int MAX_PRIORITY = 5;
    private static final int MAX_NAME = 80;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    private Task requiresExistingTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task not found.")
        );
    }

    public void verifyTaskOwnership(Task task) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String taskOwnerUsername = userService.findUserNameByUserId(task.getUserId());

        if (!currentUsername.equals(taskOwnerUsername)) {
            throw new IllegalArgumentException("Task not found."); // same as for non-existing task
        }
    }

    private Task requiresExistingTask(UUID publicTaskId) {
        Optional<Task> taskOptional = taskRepository.findByPublicId(publicTaskId);
        if (taskOptional.isPresent()) {
            return taskOptional.get();
        }
        else {
            throw new IllegalArgumentException("Task not found."); // same message as when user does not match to task to obscure if a tasks exists for another user
        }
    }

    private void validateTask(Task task) {
        if(task.getPriority() > MAX_PRIORITY || task.getPriority() < MIN_PRIORITY) {
            throw new IllegalArgumentException("Priority outside priority levels.");
        }
        if (task.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline can not be in the past.");
        }
        if (task.getName().length() > MAX_NAME) {
            throw new IllegalArgumentException("Task name to long. 80 characters max.");
        }
    }

    public void createTask(Task task) {
        validateTask(task);
        taskRepository.create(task);
    }

    public List<Task> getTasksForUser(Long userId) {
        return taskRepository.findSet(userId);
    }

    public Task getTaskById(Long taskId) {
        return requiresExistingTask(taskId);
    }

    public Task getTaskByPublicId(UUID publicTaskId) {
        return requiresExistingTask(publicTaskId);
    }

    public void updateTask(Task task) {
        validateTask(task);

        Task existingTask = requiresExistingTask(task.getPublicId());
        verifyTaskOwnership(existingTask);
        taskRepository.update(task, task.getPublicId());
    }

    public void markTaskAsCompleted(Long taskId) {
        Task task = requiresExistingTask(taskId);
        verifyTaskOwnership(task);

        task.setCompleted(true);
        taskRepository.update(task, task.getId());
    }

    public void markTaskAsCompleted(UUID publicTaskId) {
        Task task = requiresExistingTask(publicTaskId);
        verifyTaskOwnership(task);

        task.setCompleted(true);
        taskRepository.update(task, task.getPublicId());
    }

    public void deleteTask(Long taskId){
        Task delTask = requiresExistingTask(taskId);
        verifyTaskOwnership(delTask);
        taskRepository.delete(taskId);
    }

    public void deleteTask(UUID publicTaskId){
        Task delTask = requiresExistingTask(publicTaskId);
        verifyTaskOwnership(delTask);
        taskRepository.delete(publicTaskId);
    }
}
