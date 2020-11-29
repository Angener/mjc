package com.epam.esm.dao;

import com.epam.esm.entity.MostWidelyUsedTag;
import com.epam.esm.entity.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDaoImpl implements OrderDao {
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
    public List<Order> getUserOrders(int userId) {
        return (List<Order>) entityManager.createQuery("FROM Order WHERE user_id = :userId")
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> getUserOrders(int userId, int startPosition, int recordsQuantity) {
        return (List<Order>) entityManager.createQuery("FROM Order WHERE user_id = :userId")
                .setParameter("userId", userId)
                .setFirstResult(startPosition)
                .setMaxResults(recordsQuantity)
                .getResultList();
    }

    @Override
    public Order get(int userId, int orderId) {
        return (Order) entityManager.createQuery("FROM Order WHERE user_Id = :userId AND id = :orderId")
                .setParameter("userId", userId)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    @Override
    public MostWidelyUsedTag getMostWidelyUsedTag(int userId) {
        return (MostWidelyUsedTag) entityManager.createNativeQuery(
                "SELECT tag.id AS tag_id, tag.name AS tag_name, MAX (o.order_cost) AS highest_cost " +
                        "FROM tag tag " +
                        "JOIN tag_gift_certificate tgc ON tgc.tag_id = tag.id " +
                        "JOIN \"order\" o ON o.certificate_id = tgc.gift_certificate_id " +
                        "WHERE o.user_id = :userId " +
                        "GROUP BY tag.id " +
                        "ORDER BY COUNT(tag.id) DESC " +
                        "LIMIT 1",
                "mostWidelyUsedTagMapper")
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public long getOrdersQuantity() {
        return (long) entityManager.createQuery("SELECT COUNT(*) FROM Order").getSingleResult();
    }
}
