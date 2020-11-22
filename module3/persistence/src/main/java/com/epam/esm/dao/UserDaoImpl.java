package com.epam.esm.dao;

import com.epam.esm.entity.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAll() {
        return (List<User>) entityManager.createQuery("FROM User").getResultList();
    }

    @Override
    public User getByName(String name) {
        User user = (User) entityManager.createQuery("FROM User u WHERE u.name = :name")
                .setParameter("name", name)
                .getSingleResult();
        entityManager.detach(user);
        return user;

    }

    @Override
    public User getById(long id) {
        User user = entityManager.find(User.class, id);
        entityManager.detach(user);
        return user;
    }
}
