package com.example.todoapp.task.service;

import com.example.todoapp.task.Task;
import com.example.todoapp.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setPublicId(UUID.randomUUID());
        task.setName("name");
        task.setPriority(3);
        task.setDeadline(LocalDate.now().plusDays(1));
        task.setUserId(1L);
        task.setCompleted(false);
    }

    @Test
    void createTask_success() {

        assertDoesNotThrow(() -> taskService.createTask(task));
        verify(taskRepository).create(task);
    }

    @Test
    void createTask_Failure_PriorityTooHigh() {
        task.setPriority(20);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask(task));

        assertEquals("Priority outside priority levels.", ex.getMessage());
        verify(taskRepository, never()).create(any());
    }

    @Test
    void createTask_Failure_PriorityTooLow() {
        task.setPriority(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask(task));

        assertEquals("Priority outside priority levels.", ex.getMessage());
        verify(taskRepository, never()).create(any());
    }

    @Test
    void createTask_Failure_DateInPast() {
        task.setDeadline(LocalDate.now().minusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask(task));

        assertEquals("Deadline can not be in the past.", ex.getMessage());
        verify(taskRepository, never()).create(any());
    }

    @Test
    void createTask_Failure_NameTooLong() {
        task.setName("a".repeat(81));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                taskService.createTask(task));

        assertEquals("Task name to long. 80 characters max.", ex.getMessage());
        verify(taskRepository, never()).create(any());
    }


    @Test
    void getTaskByIdTest_Success() {
        UUID taskId = task.getPublicId();


        when(taskRepository.findByPublicId(taskId)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskByPublicId(taskId);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        verify(taskRepository).findByPublicId(taskId);
    }

    @Test
    void getTaskByIdTest_NotExistingTask() {
        UUID taskId = task.getPublicId();

        when(taskRepository.findByPublicId(taskId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                taskService.getTaskByPublicId(taskId));

        verify(taskRepository).findByPublicId(taskId);
    }

    @Test
    void updateTaskTest() {
        UUID taskId = task.getPublicId();

        when(taskRepository.findByPublicId(taskId)).thenReturn(Optional.of(task));

        taskService.updateTask(task);

        verify(taskRepository).update(task, taskId);
    }

    @Test
    void deleteTaskTest_Success() {
        UUID taskId = task.getPublicId();

        when(taskRepository.findByPublicId(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId);

        verify(taskRepository).delete(taskId);
    }

    @Test
    void deleteTaskTest_NotExistingTask() {
        UUID taskId = task.getPublicId();

        when(taskRepository.findByPublicId(taskId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTask(taskId));

        verify(taskRepository, never()).delete(any(UUID.class));
    }


}