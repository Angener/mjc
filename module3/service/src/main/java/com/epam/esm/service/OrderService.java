package com.epam.esm.service;

import com.epam.esm.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);

    List<Order> getUserOrders(long userId);

    Order get(long id);
}
