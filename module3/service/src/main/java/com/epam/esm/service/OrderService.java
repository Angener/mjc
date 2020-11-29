package com.epam.esm.service;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);

    List<Order> getUserOrders(long userId);

    OrderDetail get(long userId, long orderId);

    MostWidelyUsedTag getMostWidelyUsedTag(long userId);
}
