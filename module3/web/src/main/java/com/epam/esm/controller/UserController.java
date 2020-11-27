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

    @GetMapping("/users")
    public List<User> getAll() {
        try {
            return userService.getAll();
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable long id) {
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
    public List<Order> getUserOrders(@PathVariable long userId) {
        try {
            return orderService.getUserOrders(userId);
        } catch (NullPointerException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{userId}/orders/{orderId}")
    public OrderDetail getUserOrder(@PathVariable long orderId) {
        Order order = orderService.get(orderId);
        return new OrderDetail(order.getOrderDate(), order.getOrderCost());
    }

//    @GetMapping("/bestOrderTag")
//    public MostWidelyUsedTag getMostWidelyUsedTag(@RequestBody User user){
//        return orderService.getMostWidelyUsedTag(user);
//    }
}
