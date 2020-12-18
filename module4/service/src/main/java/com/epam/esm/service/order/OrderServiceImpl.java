package com.epam.esm.service.order;

import com.epam.esm.repository.OrderRepository;
import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderDetail;
import com.epam.esm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findUserOrders(int userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetail find(int userId, int orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId).orElseThrow(NoResultException::new);
        return new OrderDetail(order.getOrderDate(), order.getOrderCost());
    }

    @Override
    @Transactional(readOnly = true)
    public MostWidelyUsedTag findMostWidelyUsedTag(int userId) {
        return orderRepository.findMostWidelyUsedTag(userId);
    }
}
