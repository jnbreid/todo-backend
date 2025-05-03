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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    void getTasksForUserTest_Success() {
        Task task2 = new Task();
        task2.setName("name2");
        task2.setPriority(4);
        task2.setDeadline(LocalDate.now().plusDays(2));
        task2.setUserId(1L);
        task2.setCompleted(false);

        Long userId = task.getUserId();

        List<Task> mockTasks = List.of(task, task2);

        when(taskRepository.findSet(userId)).thenReturn(mockTasks);

        List<Task> results = taskService.getTasksForUser(userId);

        assertEquals(2, results.size());
        assertEquals("name", results.getFirst().getName());
        assertEquals(4, task2.getPriority());
        verify(taskRepository).findSet(userId);
    }

    @Test
    void getTasksForUserTest_NoTasksExist() {
        Long userId = 1L;

        when(taskRepository.findSet(userId)).thenReturn(Collections.emptyList());

        List<Task> results = taskService.getTasksForUser(userId);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(taskRepository).findSet(userId);
    }

    @Test
    void getTAskByIkTest_Success() {
        Long taskId = task.getId();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        verify(taskRepository).findById(taskId);
    }

    @Test
    void getTAskByIkTest_NotExistingTask() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                taskService.getTaskById(taskId));

        verify(taskRepository).findById(taskId);
    }

    @Test
    void updateTaskTest() {
        Long taskId = task.getId();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.updateTask(task);

        verify(taskRepository).update(task, taskId);
    }

    @Test
    void deleteTaskTest_Success() {
        Long taskId = task.getId();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId);

        verify(taskRepository).delete(taskId);
    }

    @Test
    void deleteTaskTest_NotExistingTask() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                taskService.deleteTask(taskId));

        verify(taskRepository, never()).delete(anyLong());
    }


}