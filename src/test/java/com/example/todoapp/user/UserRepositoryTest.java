package com.example.todoapp.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void createTest() {
        User user = new User(null, "username", "pswd");
        int inserted = userRepository.create(user);
        assertEquals(1, inserted);
    }

    @Test
    void FindAllTest() {
        User user = new User(null, "username", "pswd");
        userRepository.create(user);

        List<User> newUsers = userRepository.findAll();

        assertFalse(newUsers.isEmpty());
        User newUser = newUsers.getFirst();

        assertNotNull(newUser.getId());
        assertEquals(user.getUsername(), newUser.getUsername());
        assertEquals(user.getPassword(), newUser.getPassword());
    }

    @Test
    void findByUsernameTest() {
        User user = new User(null, "username", "pswd");
        userRepository.create(user);

        Optional<User> newOptional = userRepository.findByUsername(user.getUsername());

        // test if item can be found if it exists
        assertFalse(newOptional.isEmpty());

        User newUser = newOptional.get();

        assertNotNull(newUser.getId());
        assertEquals(user.getId(), newUser.getId());
        assertEquals(user.getUsername(), newUser.getUsername());
        assertEquals(user.getPassword(), newUser.getPassword());

        // test behavior if item does not exist
        Optional<User> noUser = userRepository.findByUsername("notExisting");
        assertTrue(noUser.isEmpty());
    }

    @Test
    void findByIdTest() {
        User user = new User(null, "username", "pswd");
        userRepository.create(user);

        List<User> newUsers = userRepository.findAll();
        assertFalse(newUsers.isEmpty());
        User IdUser = newUsers.getFirst();

        Optional<User> newOptional = userRepository.findById(IdUser.getId());

        // test if item can be found if it exists
        assertFalse(newOptional.isEmpty());

        User newUser = newOptional.get();

        assertNotNull(newUser.getId());
        assertEquals(user.getId(), newUser.getId());
        assertEquals(user.getUsername(), newUser.getUsername());
        assertEquals(user.getPassword(), newUser.getPassword());

        // test behavior if item does not exist
        Optional<User> noUser = userRepository.findByUsername("notExisting");
        assertTrue(noUser.isEmpty());
    }


    @Test
    void deleteTest() {
        User user = new User(null, "username", "pswd");
        userRepository.create(user);

        Optional<User> newUser = userRepository.findByUsername(user.getUsername());
        assertFalse(newUser.isEmpty());

        int delCount = userRepository.delete(user.getUsername());
        assertEquals(1, delCount);

        Optional<User> delUser = userRepository.findByUsername(user.getUsername());
        assertTrue(delUser.isEmpty());
    }
}