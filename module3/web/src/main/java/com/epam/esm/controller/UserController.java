package com.epam.esm.controller;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;
import com.epam.esm.entity.User;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public CollectionModel<User> getAll(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                        @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
        try {
            return getUserCollectionModel(page, size);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    private CollectionModel<User> getUserCollectionModel(int page, int size) {

        List<User> users = userService.getAll(page, size);
        users.forEach(this::linkUserWithItself);
        CollectionModel<User> model = CollectionModel.of(users, linkTo(methodOn(UserController.class)
                .getAll(page, size)).withSelfRel());
        paginateModel(model, page, size);
        return model;
    }

    private void linkUserWithItself(User user) {
        user.add(linkTo(methodOn(UserController.class).get(user.getId())).withSelfRel());
    }

    private void paginateModel(CollectionModel<User> model, int page, int size) {
        if (size > 0) {
            paginate(model, page, size);
        }
    }

    private void paginate(CollectionModel<User> model, int page, int size) {
        linkToPreviousPages(model, page, size);
        linkToNextPages(model, page, size);
    }

    private void linkToPreviousPages(CollectionModel<User> model, int page, int size) {
        if (page > 0) {
            int previousPage = page - 1;
            model.add(linkTo(methodOn(UserController.class).getAll(0, size)).withRel("firstPage"));
            model.add(linkTo(methodOn(UserController.class).getAll(previousPage, size)).withRel("previousPage"));
        }
    }

    private void linkToNextPages(CollectionModel<User> model, int page, int size) {
        long usersQuantity = userService.getUsersQuantity();
        if (isNextPagesExist(usersQuantity, page, size)) {
            int lastPage = findLastPageNumber(usersQuantity, size);
            model.add(linkTo(methodOn(UserController.class).getAll(page + 1, size)).withRel("nextPage"));
            model.add(linkTo(methodOn(UserController.class).getAll(lastPage, size)).withRel("lastPage"));
        }
    }

    private boolean isNextPagesExist(long recordsQuantity, int page, int size) {
        return recordsQuantity > (page + 1) * size;
    }

    private int findLastPageNumber(long recordsQuantity, int size) {
        int lastPage = (int) (recordsQuantity + size - 1) / size;
        return recordsQuantity % lastPage == 0 ? lastPage - 1 : lastPage;
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable int id) {
        try {
            return getLinkedUser(id);
        } catch (NullPointerException ex) {
            throw new LocalizedControllerException("exception.message.40403", 40403, HttpStatus.NOT_FOUND);
        }
    }

    private User getLinkedUser(int id) {
        User user = userService.getById(id);
        linkUserWithItself(user);
        return user;
    }

    @PostMapping("/users/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/users/{userId}/orders")
    public CollectionModel<Order> getUserOrders(@PathVariable int userId,
                                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
        try {
            return getOrderCollectionModel(userId, page, size);
        } catch (NoResultException ex) {
            throw new LocalizedControllerException("exception.message.40405", 40405, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    private CollectionModel<Order> getOrderCollectionModel(int userId, int page, int size) {
        List<Order> orders = orderService.getUserOrders(userId, page, size);
        orders.forEach(this::linkOrder);
        CollectionModel<Order> model = CollectionModel.of(orders, linkTo(methodOn(UserController.class)
                .getUserOrders(userId, page, size)).withSelfRel());
        paginateModel(model, page, size, userId);
        return model;
    }

    private void linkOrder(Order order) {
        linkOrderWithItself(order);
        linkOrderWithUser(order);
        linkOrderWithCertificate(order);
    }

    private void linkOrderWithItself(Order order) {
        int userId = order.getUser().getId();
        order.add(linkTo(methodOn(UserController.class).getUserOrder(userId, order.getId())).withSelfRel());
    }

    private void linkOrderWithUser(Order order) {
        order.add(linkTo(methodOn(UserController.class).get(order.getUser().getId())).withRel("user"));
    }

    private void linkOrderWithCertificate(Order order) {
        int certificateId = order.getCertificate().getId();
        order.add(linkTo(methodOn(GiftCertificateController.class).getById(certificateId)).withRel("certificate"));
    }

    private void paginateModel(CollectionModel<Order> model, int... parameters) {
        int size = parameters[1];
        if (size > 0) {
            paginate(model, parameters);
        }
    }

    private void paginate(CollectionModel<Order> model, int[] parameters) {
        linkToPreviousPages(model, parameters);
        linkToNextPages(model, parameters);
    }

    private void linkToPreviousPages(CollectionModel<Order> model, int[] parameters) {
        int page = parameters[0];
        int size = parameters[1];
        int userId = parameters[2];
        if (page > 0) {
            int previousPage = page - 1;
            model.add(linkTo(methodOn(UserController.class).getUserOrders(userId, 0, size)).withRel("firstPage"));
            model.add(linkTo(methodOn(UserController.class).getUserOrders(userId, previousPage, size))
                    .withRel("previousPage"));
        }
    }

    private void linkToNextPages(CollectionModel<Order> model, int[] parameters) {
        int page = parameters[0];
        int size = parameters[1];
        int userId = parameters[2];
        long ordersQuantity = orderService.getOrdersQuantity(userId);
        if (isNextPagesExist(ordersQuantity, page, size)) {
            int lastPage = findLastPageNumber(ordersQuantity, size);
            model.add(linkTo(methodOn(UserController.class).getUserOrders(userId, lastPage, size)).withRel("lastPage"));
            model.add(linkTo(methodOn(UserController.class).getUserOrders(userId, page + 1, size))
                    .withRel("nextPage"));
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
