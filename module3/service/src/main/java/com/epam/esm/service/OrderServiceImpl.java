package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        return orderDao.createOrder(order);
    }

    @Override
    public List<Order> getUserOrders(long userId) {
        return orderDao.getUserOrders(userId);
    }

    @Override
    public Order get(long id) {
        return orderDao.get(id);
    }
}
