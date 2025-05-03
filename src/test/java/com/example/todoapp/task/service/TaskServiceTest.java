package com.example.todoapp.task.service;

import com.example.todoapp.task.Task;
import com.example.todoapp.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_success() {
        Task task = new Task(null, "Test Task", LocalDate.now().plusDays(1), 3, false, 1L);
        taskService.createTask(task);
        verify(taskRepository).create(task);
    }

    @Test
    void createTask_fails_dueToPastDeadline() {
        Task task = new Task(null, "Test", LocalDate.now().minusDays(1), 3, false, 1L);
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).create(any());
    }

    @Test
    void getTaskById_returnsTaskIfExists() {
        Task task = new Task(1L, "Task", LocalDate.now().plusDays(1), 2, false, 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);
        assertEquals(task, result);
    }

    @Test
    void getTaskById_throwsIfNotExists() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void markTaskAsCompleted_success() {
        Task task = new Task(1L, "Task", LocalDate.now().plusDays(1), 2, false, 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.markTaskAsCompleted(1L);

        assertTrue(task.getCompleted());
        verify(taskRepository).update(task, 1L);
    }

    @Test
    void deleteTask_success() {
        Task task = new Task(1L, "Task", LocalDate.now().plusDays(1), 3, false, 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);
        verify(taskRepository).delete(1L);
    }

    @Test
    void updateTask_valid() {
        Task task = new Task(1L, "Updated Task", LocalDate.now().plusDays(2), 2, false, 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.updateTask(task);
        verify(taskRepository).update(task, 1L);
    }

    @Test
    void updateTask_invalidPriority() {
        Task task = new Task(1L, "Bad Priority", LocalDate.now().plusDays(1), 99, false, 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(task));
    }
}