package com.example.todoapp.task;

import com.example.todoapp.task.repository.TaskRepository;
import com.example.todoapp.user.User;
import com.example.todoapp.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
public class TaskRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("secret")
            .withInitScript("sql/schema.sql");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createTest() {
        User user = new User(null, "username", "pswd");
        userRepository.create(user);
        Optional<User> newOptional = userRepository.findByUsername(user.getUsername());
        assertFalse(newOptional.isEmpty());
        long userId = newOptional.get().getId();

        LocalDate localDate = LocalDate.of(2020, 6, 7);
        Task task = new Task(null, "task", localDate, 5, true, userId);
        int inserted = taskRepository.create(task);
        assertEquals(1, inserted);
    }

    @Test
    void findSetTest() {
        User user1 = new User(null, "username1", "pswd");
        User user2 = new User(null, "username2", "pswd");
        userRepository.create(user1);
        userRepository.create(user2);

        Optional<User> newOptional1 = userRepository.findByUsername(user1.getUsername());
        assertFalse(newOptional1.isEmpty());
        long userId1 = newOptional1.get().getId();
        Optional<User> newOptional2 = userRepository.findByUsername(user2.getUsername());
        assertFalse(newOptional2.isEmpty());
        long userId2 = newOptional2.get().getId();

        LocalDate localDate = LocalDate.of(2020, 6, 7);
        Task task1 = new Task(null, "task1", localDate, 5, true, userId1);
        Task task2 = new Task(null, "task2", localDate, 5, true, userId1);
        Task task3 = new Task(null, "task3", localDate, 5, true, userId2);
        taskRepository.create(task1);
        taskRepository.create(task2);
        taskRepository.create(task3);

        List<Task> p1Tasks = taskRepository.findSet(task1.getUserId());
        assertFalse(p1Tasks.isEmpty());
        assertEquals(2, p1Tasks.size());

        List<Task> p2Tasks = taskRepository.findSet(task3.getUserId());
        assertFalse(p2Tasks.isEmpty());
        assertEquals(1, p2Tasks.size());

        Task newTask = p1Tasks.getFirst();
        assertNotNull(newTask.getId());
        assertEquals(task1.getName(), newTask.getName());
        assertEquals(task1.getPriority(), newTask.getPriority());
        assertEquals(task1.getCompleted(), newTask.getCompleted());
        assertEquals(task1.getUserId(), newTask.getUserId());
        assertTrue(task1.getDeadline().equals(newTask.getDeadline()));

    }

    @Test
    void findByIdTest() {
        User user = new User(null, "username", "pswd");
        userRepository.create(user);
        Optional<User> newOptionalUser = userRepository.findByUsername(user.getUsername());
        assertFalse(newOptionalUser.isEmpty());
        long userId = newOptionalUser.get().getId();

        LocalDate localDate = LocalDate.of(2020, 6, 7);
        Task task = new Task(null, "task", localDate, 5, true, userId);
        taskRepository.create(task);

        // load set to get id
        List<Task> p1Tasks = taskRepository.findSet(task.getUserId());
        assertFalse(p1Tasks.isEmpty());
        // use id to test findById
        Optional<Task> newOptional = taskRepository.findById(p1Tasks.getFirst().getId());

        // test if item can be found if exists
        assertFalse(newOptional.isEmpty());

        Task newTask = newOptional.get();

        assertNotNull(newTask.getId());
        assertEquals(p1Tasks.getFirst().getId(), newTask.getId());
        assertEquals(task.getName(), newTask.getName());
        assertEquals(task.getPriority(), newTask.getPriority());
        assertEquals(task.getCompleted(), newTask.getCompleted());
        assertEquals(task.getUserId(), newTask.getUserId());
        assertTrue(task.getDeadline().equals(newTask.getDeadline()));


        // test behavior if item does not exist
        Optional<Task> noTask = taskRepository.findById(newTask.getId()+1);
        assertTrue(noTask.isEmpty());
    }

    @Test
    void updateTest() {
        User user = new User(null, "username", "pswd");
        userRepository.create(user);
        Optional<User> newOptional = userRepository.findByUsername(user.getUsername());
        assertFalse(newOptional.isEmpty());
        long userId = newOptional.get().getId();

        LocalDate localDate = LocalDate.of(2020, 6, 7);
        Task task = new Task(null, "task", localDate, 5, true, userId);

        taskRepository.create(task);

        User userUpdate = new User(43L, "usernameUpdate", "pswd");
        userRepository.create(userUpdate);
        Optional<User> newOptionalUpdate = userRepository.findByUsername(user.getUsername());
        assertFalse(newOptionalUpdate.isEmpty());
        long userIdUpdate = newOptionalUpdate.get().getId();
        LocalDate localDateUpdate = LocalDate.of(2021, 7, 8);
        Task taskUpdate = new Task(43L, "taskUpdate", localDateUpdate, 1, false, userIdUpdate);
        // get task id to update specified row
        List<Task> p1Tasks = taskRepository.findSet(task.getUserId());
        assertFalse(p1Tasks.isEmpty());
        int updateCount = taskRepository.update(taskUpdate, p1Tasks.getFirst().getId());
        // only one row sould be updated
        assertEquals(1, updateCount);

        // get the updated item
        Optional<Task> newOptionalTask = taskRepository.findById(p1Tasks.getFirst().getId());
        // test if item can be found if exists
        assertFalse(newOptionalTask.isEmpty());

        Task newTask = newOptionalTask.get();
        // test if new values are transferred correctly
        assertNotNull(newTask.getId());
        assertEquals(p1Tasks.getFirst().getId(), newTask.getId());
        assertEquals(taskUpdate.getName(), newTask.getName());
        assertEquals(taskUpdate.getPriority(), newTask.getPriority());
        assertEquals(taskUpdate.getCompleted(), newTask.getCompleted());
        assertEquals(taskUpdate.getUserId(), newTask.getUserId());
        assertTrue(taskUpdate.getDeadline().equals(newTask.getDeadline()));

    }

    @Test
    void deleteTest() {
        // create a user for foreign key constraint in table user_id and assert its existence
        User user = new User(null, "username", "pswd");
        userRepository.create(user);
        Optional<User> newOptional = userRepository.findByUsername(user.getUsername());
        assertFalse(newOptional.isEmpty());
        long userId = newOptional.get().getId();

        // test delete() function
        // create a task
        LocalDate localDate = LocalDate.of(2020, 6, 7);
        Task task = new Task(null, "task", localDate, 5, true, userId);
        taskRepository.create(task);
        // get all tasks for the created user to get task id
        List<Task> p1Tasks = taskRepository.findSet(task.getUserId());
        assertFalse(p1Tasks.isEmpty());
        Task newTask = p1Tasks.getFirst();

        int delCount = taskRepository.delete(newTask.getId());
        assertEquals(1, delCount);

        Optional<Task> delTask = taskRepository.findById(p1Tasks.getFirst().getId());
        assertTrue(delTask.isEmpty());

        // test deleting by removing user (owner) of task
        // first create a new task with given user as owner
        taskRepository.create(task);
        List<Task> p2Tasks = taskRepository.findSet(task.getUserId());
        assertFalse(p2Tasks.isEmpty());
        // test if task exists
        Optional<Task> cascadeDelTask = taskRepository.findById(p2Tasks.getFirst().getId());
        assertFalse(cascadeDelTask.isEmpty());
        // remove user that is owner of the task
        int delCountUser = userRepository.delete(user.getUsername());
        assertEquals(1, delCountUser);
        // check if task owned by user is removed correctly
        Optional<Task> userDeletedTask = taskRepository.findById(p2Tasks.getFirst().getId());
        assertTrue(userDeletedTask.isEmpty());
    }
}
