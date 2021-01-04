package com.epam.esm.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

public class TagRepositoryIntegrationTest extends InMemoryDbConfig {
    @Autowired
    private TagRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        super.setUp();
    }

    @AfterEach
    void destroy() {
        super.destroy();
    }

    @Test
    public void findByName() {
        assertTrue(repository.findByName("first tag").isPresent());
    }

    @Test
    public void existTagByName() {
        assertTrue(repository.existsTagByName("first tag"));
    }
}
