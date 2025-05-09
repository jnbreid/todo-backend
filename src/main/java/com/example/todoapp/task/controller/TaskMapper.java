package com.example.todoapp.task.controller;

import com.example.todoapp.task.Task;

public class TaskMapper {

    public static Task fromDTO(TaskDTO taskDTO) {
        Task task = new Task();
        task.setPublicId(taskDTO.getPublicId());
        task.setName(taskDTO.getName());
        task.setDeadline(taskDTO.getDeadline());
        task.setPriority(taskDTO.getPriority());
        task.setCompleted(taskDTO.getComplete());
        return task;
    }

    public static TaskDTO toDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setPublicId(task.getPublicId());
        taskDTO.setName(task.getName());
        taskDTO.setDeadline(task.getDeadline());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setComplete(task.getCompleted());
        return taskDTO;
    }
}
