package com.example.todoapp.task.service;

import com.example.todoapp.task.Task;
import com.example.todoapp.task.repository.TaskRepository;
import com.example.todoapp.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private final UUID publicId = UUID.randomUUID();
    private final Long userId = 1L;
    private final String username = "username";

    private Task task;

    @BeforeEach
    void setUp() {
        // simulate authentication
        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // prepare test task
        task = new Task();
        task.setId(1L);
        task.setPublicId(publicId);
        task.setName("name");
        task.setPriority(3);
        task.setDeadline(LocalDate.now().plusDays(1));
        task.setUserId(userId);
        task.setCompleted(false);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
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
    void testUpdateTask_Success() {
        Task existingTask = new Task();
        existingTask.setPublicId(publicId);
        existingTask.setUserId(userId);

        when(taskRepository.findByPublicId(publicId)).thenReturn(Optional.of(existingTask));
        when(userService.findUserNameByUserId(userId)).thenReturn(username);

        taskService.updateTask(task);

        verify(taskRepository).update(task, publicId);
    }

    @Test
    void testUpdateTask_TaskNotFound() {
        when(taskRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.updateTask(task)
        );

        assertEquals("Task not found.", exception.getMessage());
    }

    @Test
    void testUpdateTask_UnauthorizedUser() {
        when(taskRepository.findByPublicId(publicId)).thenReturn(Optional.of(task));
        when(userService.findUserNameByUserId(anyLong())).thenReturn("anotherUser");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.updateTask(task)
        );

        assertEquals("Task not found.", exception.getMessage()); // same as not found to avoid info leak
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