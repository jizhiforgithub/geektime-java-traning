package com.jizhi.springboot.cache.controller;

import com.jizhi.springboot.cache.repository.domain.User;
import com.jizhi.springboot.cache.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUser() {
        return userService.getUserAllList();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @GetMapping("/update/{id}/{age}")
    public User modifyUser(@PathVariable int id, @PathVariable int age) {
        return userService.modifyUserAgeById(id, age);
    }

    @GetMapping("/delete/{id}")
    public boolean deleteUser(@PathVariable int id) {
        return userService.deleteUserBuId(id);
    }

    @GetMapping("/get/{name}")
    public Collection<User> getUserByName(@PathVariable String name) {
        return userService.getUserByName(name);
    }
}
