package com.example.todoapp.user;

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
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        User user = UserMapper.fromDTO(userDTO);
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");

    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) {
        User authenticated = userService.authenticate(userDTO.getUsername(), userDTO.getPassword());
        UserDTO response = UserMapper.toDTO(authenticated);
        return ResponseEntity.ok(response);
    }

}
