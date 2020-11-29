package com.epam.esm.controller;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;
import com.epam.esm.entity.User;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService service, OrderService orderService) {
        this.userService = service;
        this.orderService = orderService;
    }

    @GetMapping(value = "/users")
    public List<User> getAll(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                             @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
        try {
            return userService.getAll(page, size);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable int id) {
        try {
            return userService.getById(id);
        } catch (NullPointerException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/users/{userId}/orders")
    public List<Order> getUserOrders(@PathVariable int userId,
                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                     @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
        try {
            return orderService.getUserOrders(userId, page, size);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40405", 40405, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{userId}/orders/{orderId}")
    public OrderDetail getUserOrder(@PathVariable int userId, @PathVariable int orderId) {
        try {
            return orderService.get(userId, orderId);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40404", 40404, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/bestUserOrderTag/{userId}")
    public MostWidelyUsedTag getMostWidelyUsedTag(@PathVariable int userId) {
        try {
            return orderService.getMostWidelyUsedTag(userId);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40406", 40406, HttpStatus.NOT_FOUND);
        }
    }
}
