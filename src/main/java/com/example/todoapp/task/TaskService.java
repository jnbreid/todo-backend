package com.example.todoapp.task;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private Task requiresExistingTask(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            return taskOptional.get();
        }
        else {
            throw new IllegalArgumentException("Task with ID " + taskId + "not found.");
        }
    }

    public boolean createTask(Task task) {
        if(task.getPriority() > 5 || task.getPriority() < 1) {
            throw new IllegalArgumentException("Priority outside priority levels.");
        } else if (task.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline can not be in the past.");
        }
        taskRepository.create(task);
        return true;
    }

    public List<Task> getTasksForUser(Long userId) {
        return taskRepository.findSet(userId);
    }

    public Task getTaskById(Long taskId) {
        return requiresExistingTask(taskId);

    }

    public void updateTask(Task task) {
        if(task.getPriority() > 5 || task.getPriority() < 1) {
            throw new IllegalArgumentException("Priority outside priority levels.");
        } else if (task.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline can not be in the past.");
        }

        Task foundTask = requiresExistingTask(task.getId());
        taskRepository.update(task, task.getId());
    }

    public void markTaskAsCompleted(Long taskId) {
        Task task = requiresExistingTask(taskId);

        task.setCompleted(true);
        taskRepository.update(task, task.getId());
    }

    public void deleteTask(Long taskId){
        Task task = requiresExistingTask(taskId);
        taskRepository.delete(taskId);
    }
}
