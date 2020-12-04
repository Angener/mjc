package com.epam.esm.dao;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;

import java.util.List;

public interface OrderDao {
    Order createOrder(Order order);

    List<Order> getUserOrders(int userId);

    List<Order> getUserOrders(int userId, int startPosition, int recordsQuantity);

    Order get(int userId, int orderId);

    MostWidelyUsedTag getMostWidelyUsedTag(int userId);

    long getOrdersQuantity();

    long getOrdersQuantity(int userId);
}
