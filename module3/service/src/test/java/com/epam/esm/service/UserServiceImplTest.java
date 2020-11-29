package com.epam.esm.service;

import com.epam.esm.dao.UserDaoImpl;
import com.epam.esm.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static List<User> users;

    @Mock
    UserDaoImpl dao;
    @InjectMocks
    UserServiceImpl service;

    @BeforeAll
    public static void init() {
        users = Arrays.asList(new User(1, "first"), new User(2, "second"));
    }

    @Test
    public void getAll() {
        when(dao.getAll()).thenReturn(users);

        assertEquals(service.getAll(), users);
        verify(dao).getAll();
    }

    @Test
    public void getById() {
        when(dao.getById(anyInt())).thenReturn(users.get(0));

        assertEquals(users.get(0), service.getById(anyInt()));
        verify(dao).getById(anyInt());
    }
}
