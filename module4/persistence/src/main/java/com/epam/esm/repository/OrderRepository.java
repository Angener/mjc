package com.epam.esm.repository;

import com.epam.esm.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer>, CustomizedOrderRepository {
    Page<Order> findAllByUserId(int userId, Pageable pageable);

    Optional<Order> findByUserIdAndId(int userId, int orderId);
}
