package com.example.todoapp.user.service;

import com.example.todoapp.user.User;
import com.example.todoapp.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void validateUser(User user) {
        if(user.getUsername().length() > 60) {
            throw new IllegalArgumentException("Username to long. 60 characters max.");
        }
    }

    public void registerUser(User user) {
        validateUser(user);
        Optional<User> existing = this.userRepository.findByUsername(user.getUsername());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Creating user failed.");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.create(user);
    }

    public User authenticate(String username, String rawPassword) {
        Optional<User> foundUser = userRepository.findByUsername(username).filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
        if (foundUser.isEmpty()) {
            throw new BadCredentialsException("Invalid password or username.");
        }
        return foundUser.get();

    }

    public Long findUserIdByUserName(String userName) {
        Optional<User> existing = this.userRepository.findByUsername(userName);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }
        return existing.get().getId();
    }

    public String findUserNameByUserId(Long userId) {
        Optional<User> existing = this.userRepository.findById(userId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }
        return existing.get().getUsername();
    }

    public void deleteSelf(User delUser) {
        String username = delUser.getUsername();
        String rawPassword = delUser.getPassword();

        Optional<User> foundUser = userRepository.findByUsername(username).filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
        if (foundUser.isEmpty()) {
            throw new BadCredentialsException("Invalid password or username.");
        }

        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authUsername.equals(username)) {
            throw new AccessDeniedException("You can only delete your own account.");
        }

        userRepository.delete(username);
    }
}
