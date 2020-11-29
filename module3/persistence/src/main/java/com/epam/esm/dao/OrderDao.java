package com.epam.esm.dao;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;

import java.util.List;

public interface OrderDao {
    Order createOrder(Order order);

    List<Order> getUserOrders(long userId);

    Order get(long userId, long orderId);

    MostWidelyUsedTag getMostWidelyUsedTag(long userId);
}
