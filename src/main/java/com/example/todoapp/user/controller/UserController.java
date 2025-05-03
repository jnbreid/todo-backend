package com.example.todoapp.user.controller;

import com.example.todoapp.user.User;
import com.example.todoapp.user.service.UserMapper;
import com.example.todoapp.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserDTO userDTO) {
        User user = UserMapper.fromDTO(userDTO);
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) {
        User authenticated = userService.authenticate(userDTO.getUsername(), userDTO.getPassword());
        return ResponseEntity.ok().build();
    }

}
