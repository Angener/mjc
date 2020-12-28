package com.epam.esm.service;

import com.epam.esm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Page<User> findAll(Pageable pageable);

    Optional<User> findById(int id);

    Optional<User> findByName(String name);

    User save(User user);
}
