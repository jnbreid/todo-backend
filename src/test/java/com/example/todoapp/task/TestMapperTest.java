package com.example.todoapp.task;

import com.example.todoapp.task.controller.TaskDTO;
import com.example.todoapp.task.service.TaskMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestMapperTest {

    @Test
    void fromDTOTest() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setName("name");
        taskDTO.setDeadline(LocalDate.of(2020, 10,10));
        taskDTO.setPriority(2);
        taskDTO.setComplete(true);
        taskDTO.setUserId(42L);

        Task task = TaskMapper.fromDTO(taskDTO);

        assertEquals(taskDTO.getId(), task.getId());
        assertEquals(taskDTO.getName(), task.getName());
        assertTrue(taskDTO.getDeadline().isEqual(task.getDeadline()));
        assertEquals(taskDTO.getPriority(), task.getPriority());
        assertEquals(taskDTO.getComplete(), task.getCompleted());
        assertEquals(taskDTO.getUserId(), task.getUserId());
    }

    @Test
    void toDTOTest() {
        Task task = new Task();
        task.setId(1L);
        task.setName("name");
        task.setDeadline(LocalDate.of(2020, 10,10));
        task.setPriority(2);
        task.setCompleted(true);
        task.setUserId(42L);

        TaskDTO taskDTO = TaskMapper.toDTO(task);

        assertEquals(task.getId(), taskDTO.getId());
        assertEquals(task.getName(), taskDTO.getName());
        assertTrue(task.getDeadline().isEqual(taskDTO.getDeadline()));
        assertEquals(task.getPriority(), taskDTO.getPriority());
        assertEquals(task.getCompleted(), taskDTO.getComplete());
        assertNull(taskDTO.getUserId());
    }
}
