package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class GiftCertificateTest extends InMemoryDbConfig {
    @Autowired
    private GiftCertificateDao dao;
    private GiftCertificate certificate;
    private Set<Tag> tags;

    @BeforeEach
    void setUp() throws SQLException {
        super.setUp();
        initFields();
    }

    @AfterEach
    void destroy() {
        super.destroy();
    }

    private void initFields() {
        certificate = mock(GiftCertificate.class);
        when(certificate.getName()).thenReturn("sixth");
        when(certificate.getDescription()).thenReturn("sixth gift card");
        when(certificate.getPrice()).thenReturn(new BigDecimal("23.3"));
        when(certificate.getDuration()).thenReturn(3);
        tags = new HashSet<>();
        tags.add(new Tag(1, "first tag"));
    }

    @Test
    public void save() {
        long id = dao.save(certificate, tags);
        assertEquals(6, id);
        assertEquals(certificate.getName(), dao.getByName("sixth").getName());
        when(certificate.getName()).thenReturn(null);
        assertThrows(DataIntegrityViolationException.class, () -> dao.save(certificate, tags));
        when(certificate.getName()).thenReturn("first");
        assertThrows(DuplicateKeyException.class, () -> dao.save(certificate, tags));
    }

    @Test
    public void getByName() {
        GiftCertificate testableCertificate = dao.getByName("first");

        assertEquals("first", testableCertificate.getName());
        assertEquals("first gift card", testableCertificate.getDescription());
        assertEquals(new BigDecimal("123.20"), testableCertificate.getPrice());
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                testableCertificate.getCreateDate().format(DateTimeFormatter.ISO_DATE));
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                testableCertificate.getLastUpdateDate().format(DateTimeFormatter.ISO_DATE));
        assertEquals(12, testableCertificate.getDuration());
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getByName("no name"));
    }

    @Test
    public void getAll(){
        assertEquals(5, dao.getAll().size());
    }

    @Test
    public void getById(){
        assertEquals(5, dao.getAll().size());
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getById(0));
    }

    @Test
    public void getByTagName() {
        assertEquals(1, dao.getByTagName("second tag").size());
        assertEquals(5, dao.getByTagName("first tag").size());
    }

    @Test
    public void getByPartNameOrDescriptionTest() {
        assertEquals(2, dao.searchByPartNameOrDescription("ir").size());
    }

    @Test
    public void update() {
        String[] fields = {"name", "description", "price", "duration"};
        Set<Tag> updatableTag = new HashSet<>();
        updatableTag.add(new Tag("second tag"));
        updatableTag.add(new Tag("third tag"));
        updatableTag.add(new Tag("fourth tag"));
        when(certificate.getId()).thenReturn(1L);
        dao.update(certificate, fields, updatableTag);
        GiftCertificate testableCertificate = dao.getByName("sixth");

        assertEquals(4, dao.getByTagName("first tag").size());
        assertEquals(1, dao.getByTagName("third tag").size());
        assertEquals(1, dao.getByTagName("fourth tag").size());
        assertEquals(1, testableCertificate.getId());
        assertEquals("sixth", testableCertificate.getName());
        assertEquals("sixth gift card", testableCertificate.getDescription());
        assertEquals(new BigDecimal("23.30"), testableCertificate.getPrice());
        assertEquals(3, testableCertificate.getDuration());
        assertThrows(EmptyResultDataAccessException.class, () -> dao.getByName("first"));
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                testableCertificate.getCreateDate().format(DateTimeFormatter.ISO_DATE));
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                testableCertificate.getLastUpdateDate().format(DateTimeFormatter.ISO_DATE));
    }

    @Test
    public void updateDuplicateKeyException() {
        String[] fields = {"name"};
        when(certificate.getId()).thenReturn(5L);
        when(certificate.getName()).thenReturn("first");

        assertThrows(DuplicateKeyException.class,
                () -> dao.update(certificate, fields, tags));
    }

    @Test
    public void delete() {
        when(certificate.getId()).thenReturn(1L);
        dao.delete(certificate);
        assertEquals(4, dao.getByTagName("first tag").size());
    }
}
