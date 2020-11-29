package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
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
        List<Order> orders = orderDao.getUserOrders(userId);
        checkOrders(orders);
        return orderDao.getUserOrders(userId);
    }

    private void checkOrders(List<Order> orders) {
        if (orders.size() == 0) {
            throw new NoResultException();
        }
    }

    @Override
    public OrderDetail get(long userId, long orderId) {
        Order order = orderDao.get(userId, orderId);
        return new OrderDetail(order.getOrderDate(), order.getOrderCost());
    }

    @Override
    public MostWidelyUsedTag getMostWidelyUsedTag(long userId) {
        return orderDao.getMostWidelyUsedTag(userId);
    }
}
