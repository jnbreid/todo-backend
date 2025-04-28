package com.example.todoapp.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    void createFindAllTest() {
        User user = new User(null, "username", "pswd");
        userRepository.create(user);

        List<User> newUsers = userRepository.findAll();

        assertFalse(newUsers.isEmpty());
        User newUser = newUsers.getFirst();

        assertNotNull(newUser.getId());
        assertEquals(user.getUsername(), newUser.getUsername());
        assertEquals(user.getPassword(), newUser.getPassword());
    }
}
