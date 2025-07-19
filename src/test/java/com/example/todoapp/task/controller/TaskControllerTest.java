/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.task.controller;

import com.example.todoapp.config.JacksonConfig;
import com.example.todoapp.config.RestTestConfig;
import com.example.todoapp.config.security.CustomUserDetailsService;
import com.example.todoapp.config.security.JwtTokenUtil;
import com.example.todoapp.config.security.SecurityConfig;
import com.example.todoapp.task.Task;
import com.example.todoapp.task.service.TaskService;
import com.example.todoapp.user.controller.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO adjust test to JwtToken

@WebMvcTest(controllers = TaskController.class,
        excludeFilters = @ComponentScan.Filter(
                type  = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        ))
@Import({RestTestConfig.class, JacksonConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;


    private Task task;
    private TaskDTO taskDTO;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setPublicId(UUID.randomUUID());
        task.setName("name");
        task.setPriority(2);
        task.setDeadline(LocalDate.now().plusDays(1));
        task.setCompleted(false);
        task.setUserId(1L);

        taskDTO = new TaskDTO();
        taskDTO.setPublicId(task.getPublicId());
        taskDTO.setName(task.getName());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setDeadline(task.getDeadline());
        taskDTO.setComplete(task.getCompleted());
        taskDTO.setUserName("username");



        jwtToken = "fake-jwt-token";
    }

    @Test
    //@WithMockUser(username = "username", roles = {"USER"})
    void createTask_Success() throws Exception {
        when(taskMapper.fromDTO(taskDTO)).thenReturn(task);
        doNothing().when(taskService).createTask(task);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isCreated());

    }

    @Test
    //@WithMockUser(username = "username", roles = {"USER"})
    void createTaskTest_Failure() throws Exception {
        // deadline in the past gives IllegalArgumentException
        taskDTO.setDeadline(LocalDate.now().minusDays(1));
        task.setDeadline(taskDTO.getDeadline());

        when(taskMapper.fromDTO(any(TaskDTO.class))).thenReturn(task);
        doThrow(new IllegalArgumentException("Deadline can not be in the past."))
                .when(taskService).createTask(any(Task.class));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Deadline can not be in the past."));

    }


    @Test
    void getTaskByIdTest_Success() throws Exception {
        UUID publicTaskId = task.getPublicId();

        when(taskService.getTaskByPublicId(publicTaskId)).thenReturn(task);

        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        mockMvc.perform(get("/api/tasks/public/{public_id}", publicTaskId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(taskDTO.getPublicId().toString()))
                .andExpect(jsonPath("$.name").value(taskDTO.getName()))
                .andExpect(jsonPath("$.priority").value(taskDTO.getPriority()))
                .andExpect(jsonPath("$.complete").value(taskDTO.getComplete()))
                .andExpect(jsonPath("$.userName").value(taskDTO.getUserName()))
                .andExpect(jsonPath("$.deadline").value(taskDTO.getDeadline().toString()));
    }

    @Test
    @WithMockUser(username = "username", roles = {"USER"})
    void getMyTasks_Success() throws Exception {

        Task task2 = new Task();
        task2.setId(2L);
        task2.setPublicId(UUID.randomUUID());
        task2.setName("name2");
        task2.setPriority(3);
        task2.setDeadline(LocalDate.now().plusDays(2));
        task2.setCompleted(true);
        task2.setUserId(1L);

        TaskDTO taskDTO2 = new TaskDTO();
        taskDTO2.setPublicId(task2.getPublicId());
        taskDTO2.setName(task2.getName());
        taskDTO2.setPriority(task2.getPriority());
        taskDTO2.setDeadline(task2.getDeadline());
        taskDTO2.setComplete(task2.getCompleted());
        taskDTO2.setUserName("username");

        Long userId = task.getUserId();

        List<Task> taskList = List.of(task, task2);

        when(taskMapper.getUserId()).thenReturn(task.getUserId());
        when(taskService.getTasksForUser(userId)).thenReturn(taskList);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);
        when(taskMapper.toDTO(task2)).thenReturn(taskDTO2);

        mockMvc.perform(get("/api/tasks/my-tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(taskDTO.getName()))
                .andExpect(jsonPath("$[1].deadline").value(taskDTO2.getDeadline().toString()));
    }
}