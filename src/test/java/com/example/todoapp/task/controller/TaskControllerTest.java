package com.example.todoapp.task.controller;

import com.example.todoapp.config.JacksonConfig;
import com.example.todoapp.config.security.JwtTokenUtil;
import com.example.todoapp.config.security.SecurityConfig;
import com.example.todoapp.task.Task;
import com.example.todoapp.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO adjust test to JwtToken

@WebMvcTest(TaskController.class)
@Import({SecurityConfig.class, JacksonConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;
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
        taskDTO = taskMapper.toDTO(task);
    }

    @WithMockUser(username = "username", roles = {"USER"})
    @Test
    void createTaskTest_Success() throws Exception {

        String json_payload = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json_payload))
                .andExpect(status().isCreated());

    }

    @Test
    void createTaskTest_Failure() throws Exception {
        // deadline in the past gives IllegalArgumentException
        taskDTO.setDeadline(LocalDate.now().minusDays(1));

        doThrow(new IllegalArgumentException("Deadline can not be in the past."))
                .when(taskService).createTask(any(Task.class));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void getTaskByIdTest_Success() throws Exception {
        UUID publicTaskId = task.getPublicId();

        when(taskService.getTaskByPublicId(publicTaskId)).thenReturn(task);

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
    void getMyTasks_Success() throws Exception {
        Task task2 = new Task();
        task2.setId(2L);
        task2.setPublicId(UUID.randomUUID());
        task2.setName("name2");
        task2.setPriority(3);
        task2.setDeadline(LocalDate.now().plusDays(2));
        task2.setCompleted(true);
        task2.setUserId(1L);
        TaskDTO taskDTO2 = taskMapper.toDTO(task2);

        Long userId = task.getUserId();

        List<Task> taskList = List.of(task, task2);

        when(taskService.getTasksForUser(userId)).thenReturn(taskList);

        mockMvc.perform(get("/api/tasks/my-tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[1].deadline").value(taskDTO2.getDeadline().toString()));
    }

    @Test
    void getTasksForUser_Success_EmptyList() throws Exception {
        Long userId = task.getUserId();

        List<Task> taskList = new ArrayList<>();

        when(taskService.getTasksForUser(userId)).thenReturn(taskList);

        mockMvc.perform(get("/api/tasks/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }



    @Test
    void updateTask() throws Exception {
        mockMvc.perform(put("/api/tasks/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void maskTaskAsCompleted() throws Exception {
        UUID publicUpdateId = UUID.randomUUID();

        mockMvc.perform(patch("/api/tasks/public/{public_id}/complete", publicUpdateId.toString()))
                .andExpect(status().isOk());

    }

    @Test
    void deleteTaskTest_Success() throws Exception {

        UUID publicDeleteId = UUID.randomUUID();

        mockMvc.perform(delete("/api/tasks/public/{public_id}", publicDeleteId))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTaskTest_Failure() throws Exception {

        doThrow(new IllegalArgumentException())
                .when(taskService).deleteTask(any(UUID.class));

        UUID publicDeleteId = UUID.randomUUID();

        mockMvc.perform(delete("/api/tasks/public/{id}", publicDeleteId))
                .andExpect(status().isBadRequest());
    }
}