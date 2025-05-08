package com.example.todoapp.task.controller;

import com.example.todoapp.task.Task;
import com.example.todoapp.task.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody TaskDTO taskDTO) {
        Task task = TaskMapper.fromDTO(taskDTO);
        taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/public/{public_id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable UUID public_id) {
        Task task = taskService.getTaskByPublicId(public_id);
        TaskDTO dto = TaskMapper.toDTO(task);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<TaskDTO>> getTasksForUser(@PathVariable String userName) {
        List<Task> tasks = taskService.getTasksForUser(userName);
        List<TaskDTO> taskDTOs = tasks.stream().map(TaskMapper::toDTO).toList();
        return ResponseEntity.ok(taskDTOs);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateTask(@RequestBody TaskDTO taskDTO) {
        Task task = TaskMapper.fromDTO(taskDTO);
        taskService.updateTask(task);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/public/{public_id}/complete")
    public ResponseEntity<Void> markTaskAsCompleted(@PathVariable UUID public_id) {
        taskService.markTaskAsCompleted(public_id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/public/{public_id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID public_id) {
        taskService.deleteTask(public_id);
        return ResponseEntity.ok().build();
    }
}