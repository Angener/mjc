package com.epam.esm.service;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order save(Order order);

    Page<Order> findUserOrders(int userId, Pageable pageable);

    OrderDetail find(int userId, int orderId);

    MostWidelyUsedTag findMostWidelyUsedTag(int userId);
}
