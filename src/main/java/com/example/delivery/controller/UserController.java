package com.example.delivery.controller;

import com.example.delivery.entity.User;
import com.example.delivery.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    @GetMapping
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user){
        return userRepository.save(user);
    }

}
