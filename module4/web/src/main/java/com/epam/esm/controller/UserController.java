package com.epam.esm.controller;

import com.epam.esm.model.assembler.OrderModelAssembler;
import com.epam.esm.model.assembler.UserModelAssembler;
import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;
import com.epam.esm.entity.User;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.model.OrderModel;
import com.epam.esm.model.UserModel;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {
    private final UserService userService;
    private final OrderService orderService;
    private final PagedResourcesAssembler<User> userPagedResourcesAssembler;
    private final UserModelAssembler userModelAssembler;
    private final PagedResourcesAssembler<Order> orderPagedResourcesAssembler;
    private final OrderModelAssembler orderModelAssembler;

    @Autowired
    public UserController(UserService service, OrderService orderService,
                          PagedResourcesAssembler<User> userPagedResourcesAssembler,
                          UserModelAssembler userModelAssembler,
                          PagedResourcesAssembler<Order> orderPagedResourcesAssembler,
                          OrderModelAssembler orderModelAssembler) {
        this.userService = service;
        this.orderService = orderService;
        this.userPagedResourcesAssembler = userPagedResourcesAssembler;
        this.userModelAssembler = userModelAssembler;
        this.orderPagedResourcesAssembler = orderPagedResourcesAssembler;
        this.orderModelAssembler = orderModelAssembler;
    }

    @GetMapping(value = "/users")
    public PagedModel<UserModel> findAll(Pageable pageable) {
        try {
            return userPagedResourcesAssembler.toModel(userService.findAll(pageable), userModelAssembler);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{id}")
    public UserModel findById(@PathVariable int id) {
        return userModelAssembler.toModel(userService.findById(id).orElseThrow(() ->
                new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/users/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody Order order) {
        return orderService.save(order);
    }

    @GetMapping("/users/{userId}/orders")
    public PagedModel<OrderModel> getUserOrders(@PathVariable int userId, Pageable pageable) {
        try {
            return orderPagedResourcesAssembler.toModel(orderService.findUserOrders(userId, pageable),
                    orderModelAssembler);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40405", 40405, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{userId}/orders/{orderId}")
    public OrderDetail getUserOrder(@PathVariable int userId, @PathVariable int orderId) {
        try {
            return orderService.find(userId, orderId);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40404", 40404, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/bestUserOrderTag/{userId}")
    public MostWidelyUsedTag getMostWidelyUsedTag(@PathVariable int userId) {
        try {
            MostWidelyUsedTag tag = orderService.findMostWidelyUsedTag(userId);
            tag.add(linkTo(methodOn(TagController.class).findById(tag.getMostWidelyUsedTag().getId())).withRel("Tag"));
            return tag;
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40406", 40406, HttpStatus.NOT_FOUND);
        }
    }
}
