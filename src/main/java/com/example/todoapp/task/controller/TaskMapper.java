package com.example.todoapp.task.controller;

import com.example.todoapp.task.Task;
import com.example.todoapp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    private final UserService userService;

    @Autowired
    public TaskMapper(UserService userService) {
        this.userService = userService;
    }

    public Task fromDTO(TaskDTO taskDTO) {
        Task task = new Task();
        task.setPublicId(taskDTO.getPublicId());
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setDeadline(taskDTO.getDeadline());
        task.setPriority(taskDTO.getPriority());
        task.setCompleted(taskDTO.getComplete());

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userService.findUserIdByUserName(userName);
        task.setUserId(userId);
        return task;
    }

    public TaskDTO toDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setPublicId(task.getPublicId());
        taskDTO.setName(task.getName());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDeadline(task.getDeadline());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setComplete(task.getCompleted());

        String userName = userService.findUserNameByUserId(task.getUserId());
        taskDTO.setUserName(userName);

        return taskDTO;
    }

    public Long getUserId() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserIdByUserName(userName);
    }

    public String getUserNameFromUserId(Long userId) {
        return userService.findUserNameByUserId(userId);
    }
}
