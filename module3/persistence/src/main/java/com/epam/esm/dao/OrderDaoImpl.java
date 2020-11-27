package com.epam.esm.dao;

import com.epam.esm.entity.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDaoImpl implements OrderDao{
    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Order createOrder(Order order) {
       entityManager.persist(order);
       return order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> getUserOrders(long userId){
        return (List<Order>) entityManager.createQuery("FROM Order WHERE user_id = :userId")
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Order get(long id){
        return entityManager.find(Order.class, id);
    }
}
