package com.epam.esm.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest extends InMemoryDbConfig {
    @Autowired
    private UserDao dao;

    @BeforeEach
    void setUp() throws SQLException {
        super.setUp();
    }

    @AfterEach
    void destroy() {
        super.destroy();
    }

    @Test
    public void getAll() {
        assertEquals(2, dao.getAll().size());
        assertEquals(1, dao.getAll().get(0).getId());
        assertEquals("user2", dao.getAll().get(1).getName());
    }

    @Test
    public void getByName() {
        assertEquals(1, dao.getByName("user1").getId());
        assertThrows(NoResultException.class, () -> dao.getByName("any user"));
    }

    @Test
    public void getById() {
        assertEquals(1, dao.getById(1).getId());
        assertThrows(NoResultException.class, () -> dao.getByName("any user"));
    }
}
