/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 Jon Breid
 */

package com.example.todoapp.task.controller;

import com.example.todoapp.task.Task;
import com.example.todoapp.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskMapperTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskMapper taskMapper;

    @BeforeAll
    static void setup() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void fromDTOTest() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setPublicId(UUID.randomUUID());
        taskDTO.setName("name");
        taskDTO.setDescription("description");
        taskDTO.setDeadline(LocalDate.of(2020, 10,10));
        taskDTO.setPriority(2);
        taskDTO.setComplete(true);
        taskDTO.setUserName("testuser");

        when(userService.findUserIdByUserName(taskDTO.getUserName()))
                .thenReturn(1L);

        Task task = taskMapper.fromDTO(taskDTO);

        assertNull(task.getId());
        assertEquals(taskDTO.getPublicId(), task.getPublicId());
        assertEquals(taskDTO.getName(), task.getName());
        assertEquals(taskDTO.getDescription(), task.getDescription());
        assertTrue(taskDTO.getDeadline().isEqual(task.getDeadline()));
        assertEquals(taskDTO.getPriority(), task.getPriority());
        assertEquals(taskDTO.getComplete(), task.getCompleted());
    }

    @Test
    void toDTOTest() {
        Task task = new Task();
        task.setId(1L);
        task.setPublicId(UUID.randomUUID());
        task.setName("name");
        task.setDescription("description");
        task.setDeadline(LocalDate.of(2020, 10,10));
        task.setPriority(2);
        task.setCompleted(true);
        task.setUserId(42L);

        when(userService.findUserNameByUserId(task.getUserId()))
                .thenReturn("testuser");

        TaskDTO taskDTO = taskMapper.toDTO(task);

        assertEquals(task.getPublicId(), taskDTO.getPublicId());
        assertEquals(task.getName(), taskDTO.getName());
        assertEquals(task.getDescription(), taskDTO.getDescription());
        assertTrue(task.getDeadline().isEqual(taskDTO.getDeadline()));
        assertEquals(task.getPriority(), taskDTO.getPriority());
        assertEquals(task.getCompleted(), taskDTO.getComplete());
    }
}
