package com.example.todoapp.task;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(Task task) {
        // validation stuff

        taskRepository.create(task);
    }

    public List<Task> getTasksForUser(Long userId) {
        return taskRepository.findSet(userId);
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public boolean updateTask(Task task) {
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
