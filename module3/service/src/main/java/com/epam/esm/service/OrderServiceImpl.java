package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;
import com.google.common.base.Preconditions;
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
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(int userId) {
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
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(int userId, int page, int recordsPerPage) {
        return recordsPerPage > 0 ? getPaginateUserOrders(userId, page, recordsPerPage) : getUserOrders(userId);
    }

    private List<Order> getPaginateUserOrders(int userId, int page, int recordsPerPage) {
        int startPosition = getStartPosition(page, recordsPerPage);
        int recordsQuantity = getRecordsQuantity(startPosition, recordsPerPage);
        return checkResultOrderList(orderDao.getUserOrders(userId, startPosition, recordsQuantity));
    }

    private int getStartPosition(int page, int recordsPerPage) {
        return (page == 0) ? (0) : (page * recordsPerPage);
    }

    private int getRecordsQuantity(int startPosition, int recordsPerPage) {
        int menuSize = (int) orderDao.getOrdersQuantity() - startPosition;
        int recordsQuantity = Math.min(recordsPerPage, menuSize);
        Preconditions.checkArgument(recordsQuantity > 0);
        return recordsQuantity;
    }

    private List<Order> checkResultOrderList(List<Order> orders) {
        Preconditions.checkArgument(orders.size() > 0);
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetail get(int userId, int orderId) {
        Order order = orderDao.get(userId, orderId);
        return new OrderDetail(order.getOrderDate(), order.getOrderCost());
    }

    @Override
    @Transactional(readOnly = true)
    public MostWidelyUsedTag getMostWidelyUsedTag(int userId) {
        return orderDao.getMostWidelyUsedTag(userId);
    }
}
