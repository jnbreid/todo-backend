package com.example.todoapp.user;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {//,
                       //PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        //this.passwordEncoder = passwordEncoder;
    }

    private void validateUser(User user) {
        if(user.getUsername().length() > 60) {
            throw new IllegalArgumentException("Username name to long. 60 characters max.");
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
        //Optional<User> foundUser = userRepository.findByUsername(username).filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
        //if (foundUser.isEmpty()) {
        //    throw new BadCredentialsException("Invalid password or username.");
        //}
        //return foundUser.get();

        Optional<User> foundUser = userRepository.findByUsername(username);
        if (foundUser.isPresent() && foundUser.get().getUsername().equals(rawPassword)) {
            return foundUser.get();
        }
        else {
            throw new BadCredentialsException("Invalid password or username.");
        }
    }
}
