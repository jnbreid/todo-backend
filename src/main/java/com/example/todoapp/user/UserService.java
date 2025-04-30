package com.example.todoapp.user;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(User user) {
        Optional<User> existing = this.userRepository.findByUsername(user.getUsername());
        if (existing.isPresent()) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.create(user);
        return true;
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username).filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }
}
