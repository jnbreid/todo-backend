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

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public boolean updateTask(Task task) {
        if(task.getPriority() > 5 || task.getPriority() < 1) {
            throw new IllegalArgumentException("Priority outside priority levels.");
        } else if (task.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline can not be in the past.");
        }

        Optional<Task> existingTask = taskRepository.findById(task.getId());
        if(existingTask.isPresent()) {
            taskRepository.update(task, task.getId());
            return true;
        }
        return false;
    }

    public boolean markTaskAsCompleted(Long taskId) {
        Optional<Task> existingTask = taskRepository.findById(taskId);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setCompleted(true);
            taskRepository.update(task, task.getId());
            return true;
        }
        return false;
    }

    public boolean deleteTask(Long taskId){
        Optional<Task> existingTask = taskRepository.findById(taskId);
        if (existingTask.isPresent()) {
            taskRepository.delete(taskId);
            return true;
        }
        return false;
    }

}
