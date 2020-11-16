package com.epam.esm.dao;

import com.epam.esm.entity.User;

import java.util.List;

public interface UserDao {
    List<User> getAll();

    User getByName(String name);

    User getById(long id);
}
