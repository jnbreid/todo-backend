package com.example.todoapp.task.service;

import com.example.todoapp.task.Task;
import com.example.todoapp.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    private static final int MIN_PRIORITY = 1;
    private static final int MAX_PRIORITY = 5;
    private static final int MAX_NAME = 80;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private Task requiresExistingTask(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            return taskOptional.get();
        }
        else {
            throw new IllegalArgumentException("Task with ID " + taskId + " not found.");
        }
    }

    private Task requiresExistingTask(UUID publicTaskId) {
        Optional<Task> taskOptional = taskRepository.findByPublicId(publicTaskId);
        if (taskOptional.isPresent()) {
            return taskOptional.get();
        }
        else {
            throw new IllegalArgumentException("Task with public ID " + publicTaskId.toString() + " not found.");
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

    public List<Task> getTasksForUser(String userName) {
        return taskRepository.findSet(userName);
    }

    public Task getTaskById(Long taskId) {
        return requiresExistingTask(taskId);
    }

    public Task getTaskByPublicId(UUID publicTaskId) {
        return requiresExistingTask(publicTaskId);
    }

    public void updateTask(Task task) {
        validateTask(task);

        requiresExistingTask(task.getPublicId());
        taskRepository.update(task, task.getPublicId());
    }

    public void markTaskAsCompleted(Long taskId) {
        Task task = requiresExistingTask(taskId);

        task.setCompleted(true);
        taskRepository.update(task, task.getId());
    }

    public void markTaskAsCompleted(UUID publicTaskId) {
        Task task = requiresExistingTask(publicTaskId);

        task.setCompleted(true);
        taskRepository.update(task, task.getPublicId());
    }

    public void deleteTask(Long taskId){
        requiresExistingTask(taskId);
        taskRepository.delete(taskId);
    }

    public void deleteTask(UUID publicTaskId){
        requiresExistingTask(publicTaskId);
        taskRepository.delete(publicTaskId);
    }
}
