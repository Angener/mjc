package com.epam.esm.controller;

import com.epam.esm.entity.User;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public List<User> getAll() {
        try {
            return service.getAll();
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable long id) {
        try {
            return service.getById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        }
    }
}
