package com.epam.esm.service;

import com.epam.esm.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    List<User> getAll(int page, int recordsPerPage);

    User getById(int id);
}
