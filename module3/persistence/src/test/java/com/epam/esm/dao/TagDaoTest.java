package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

public class TagDaoTest extends InMemoryDbConfig {
    @Autowired
    private TagDao dao;

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
        assertEquals("second tag", dao.getAll().get(1).getName());
    }

    @Test
    public void getByName() {
        assertEquals(1, dao.getByName("first tag").getId());
        assertThrows(NoResultException.class, () -> dao.getByName("ninth tag"));
    }

    @Test
    public void getById() {
        assertEquals(1, dao.getById(1).getId());
        assertThrows(NoResultException.class, () -> dao.getByName("ninth tag"));
    }

    @Test
    public void getAllGiftCertificateTags() {
        GiftCertificate certificate = mock(GiftCertificate.class);
        when(certificate.getId()).thenReturn(1L);

        assertEquals(2, dao.getAllGiftCertificateTags(certificate).size());
    }

    @Test
    public void save() {
        Tag tag = dao.save(new Tag("third tag"));

        assertEquals(3, dao.getAll().size());
        assertEquals(tag, dao.getById(3));
        assertThrows(PersistenceException.class, () -> dao.save(new Tag()));
        assertThrows(PersistenceException.class, () -> dao.save(new Tag("third tag")));
    }

    @Test
    public void delete() {
        dao.delete(new Tag(1, "first tag"));
        assertEquals(1, dao.getAll().size());
    }
}
