package com.example.todoapp.task;

import com.example.todoapp.config.JacksonConfig;
import com.example.todoapp.config.SecurityConfig;
import com.example.todoapp.task.controller.TaskController;
import com.example.todoapp.task.controller.TaskDTO;
import com.example.todoapp.task.controller.TaskMapper;
import com.example.todoapp.task.service.TaskService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.SerializationFeature;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {

        task = new Task();
        task.setId(1L);
        task.setName("username");
        task.setPriority(2);
        task.setDeadline(LocalDate.of(2026, 1, 1));//LocalDate.now().plusDays(1));
        task.setCompleted(false);
        task.setUserId(1L);
        taskDTO = TaskMapper.toDTO(task);
    }

    @Test
    void createTask_Success() throws Exception {
        String json_payload = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json_payload))
                .andExpect(status().isCreated());

        Mockito.verify(taskService).createTask(any(Task.class));
    }


}