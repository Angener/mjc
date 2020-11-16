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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GiftCertificateTest extends InMemoryDbConfig {
    @Autowired
    private GiftCertificateDao dao;
    private GiftCertificate certificate;
    private Set<Tag> tags;
    List<String> sortTypes;

    private final Comparator<GiftCertificate> DATE_COMPARATOR = Comparator.comparing(GiftCertificate::getCreateDate);
    private final Comparator<GiftCertificate> NAME_COMPARATOR = Comparator.comparing(GiftCertificate::getName);

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
        sortTypes = new ArrayList<>();
    }

    @Test
    public void save() {
        GiftCertificate savedCertificate = dao.save(certificate, tags);
        assertEquals(savedCertificate, dao.getById(6));
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
        assertEquals(1, dao.getByTagName(sortTypes,"second tag").size());
        assertEquals(5, dao.getByTagName(sortTypes, "first tag").size());
    }

    @Test
    public void searchByTagAndPartNameOrDescription(){
        assertEquals(4, dao.searchByTagAndPartNameOrDescription(sortTypes, "second tag", "th").size());
    }

    @Test
    public void getByPartNameOrDescriptionTest() {
        assertEquals(2, dao.searchByPartNameOrDescription(sortTypes, "ir").size());
    }

    @Test
    public void update() {
        Map<String, Object> updatableInfo = new HashMap<>();
        updatableInfo.put("id", 1L);
        updatableInfo.put("name", "sixth");
        updatableInfo.put("description", "sixth gift card");
        updatableInfo.put("price", new BigDecimal("23.30"));
        updatableInfo.put("duration", 3);

        Set<Tag> updatableTag = new HashSet<>();
        updatableTag.add(new Tag(2, "second tag"));
        updatableTag.add(new Tag("third tag"));
        updatableTag.add(new Tag("fourth tag"));

        dao.update(updatableInfo, updatableTag);
        updatableInfo.replace("name", "sixth", "second");
        GiftCertificate testableCertificate = dao.getByName("sixth");

        assertEquals(4, dao.getByTagName(sortTypes, "first tag").size());
        assertEquals(1, dao.getByTagName(sortTypes, "third tag").size());
        assertEquals(1, dao.getByTagName(sortTypes, "fourth tag").size());
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
        assertThrows(DuplicateKeyException.class,
                () -> dao.update(updatableInfo, tags));
    }

    @Test
    public void delete() {
        when(certificate.getId()).thenReturn(1L);
        dao.delete(certificate);
        assertEquals(4, dao.getByTagName(sortTypes, "first tag").size());
    }

    @Test
    public void sortByNameAndDete(){
        List<GiftCertificate> certificates = dao.getByTagName(sortTypes, "first tag");
        sortTypes.add("create_date");
        sortTypes.add("name");
        certificates.sort(DATE_COMPARATOR.thenComparing(NAME_COMPARATOR));
        assertEquals(certificates, dao.getByTagName(sortTypes, "first tag"));
    }

    @Test
    public void sortByName(){
        List<GiftCertificate> certificates = dao.getByTagName(sortTypes, "first tag");
        sortTypes.add("name");
        certificates.sort(NAME_COMPARATOR);
        assertEquals(certificates, dao.getByTagName(sortTypes, "first tag"));
    }

    @Test
    public void sortByDate(){
        List<GiftCertificate> certificates = dao.getByTagName(sortTypes, "first tag");
        sortTypes.add("create_date");
        certificates.sort(DATE_COMPARATOR);
        assertEquals(certificates, dao.getByTagName(sortTypes, "first tag"));
    }
}
