package com.epam.esm.service;

import com.epam.esm.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(long id);
}
