package com.epam.esm.service;

import com.epam.esm.repository.UserRepository;
import com.epam.esm.entity.User;
import com.epam.esm.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static List<User> users;

    @Mock
    UserRepository repository;
    @InjectMocks
    UserServiceImpl service;

    @BeforeAll
    public static void init() {
        users = Arrays.asList(new User(1, "first"), new User(2, "second"));
    }

    @Test
    public void getAll() {
        Page<User> pageUsers = new PageImpl<>(users);
        when(repository.findAll(Pageable.unpaged())).thenReturn(pageUsers);

        assertEquals(service.findAll(Pageable.unpaged()), pageUsers);
        verify(repository).findAll(Pageable.unpaged());
    }

    @Test
    public void getById() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(users.get(0)));

        assertEquals(Optional.of(users.get(0)), service.findById(anyInt()));
        verify(repository).findById(anyInt());
    }

    @Test
    public void save() {
        when(repository.save(users.get(0))).thenReturn(users.get(0));

        assertEquals(service.save(users.get(0)), users.get(0));
        verify(repository).save(users.get(0));
    }
}
