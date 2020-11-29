package com.epam.esm.dao;

import com.epam.esm.entity.User;

import java.util.List;

public interface UserDao {
    List<User> getAll();

    List<User> getAll(int startPosition, int recordsQuantity);

    User getByName(String name);

    User getById(int id);

    long getUsersQuantity();
}
