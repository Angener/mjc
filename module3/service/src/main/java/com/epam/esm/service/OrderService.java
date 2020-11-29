package com.epam.esm.service;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);

    List<Order> getUserOrders(int userId);

    List<Order> getUserOrders(int userId, int page, int recordsPerPage);

    OrderDetail get(int userId, int orderId);

    MostWidelyUsedTag getMostWidelyUsedTag(int userId);
}
