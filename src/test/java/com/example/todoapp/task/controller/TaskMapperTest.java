package com.example.todoapp.task.controller;

import com.example.todoapp.task.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TaskMapperTest {

    @Test
    void fromDTOTest() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setPublicId(UUID.randomUUID());
        taskDTO.setName("name");
        taskDTO.setDeadline(LocalDate.of(2020, 10,10));
        taskDTO.setPriority(2);
        taskDTO.setComplete(true);
        taskDTO.setUserName("userName");

        Task task = TaskMapper.fromDTO(taskDTO);

        assertNull(task.getId());
        assertEquals(taskDTO.getPublicId(), task.getPublicId());
        assertEquals(taskDTO.getName(), task.getName());
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
        task.setDeadline(LocalDate.of(2020, 10,10));
        task.setPriority(2);
        task.setCompleted(true);
        task.setUserId(42L);

        TaskDTO taskDTO = TaskMapper.toDTO(task);

        assertEquals(task.getPublicId(), taskDTO.getPublicId());
        assertEquals(task.getName(), taskDTO.getName());
        assertTrue(task.getDeadline().isEqual(taskDTO.getDeadline()));
        assertEquals(task.getPriority(), taskDTO.getPriority());
        assertEquals(task.getCompleted(), taskDTO.getComplete());
    }
}
