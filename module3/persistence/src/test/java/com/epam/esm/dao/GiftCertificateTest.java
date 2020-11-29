package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GiftCertificateTest extends InMemoryDbConfig {
    @Autowired
    private GiftCertificateDao dao;
    private GiftCertificate certificate;
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
        certificate = new GiftCertificate();
        certificate.setName("sixth");
        certificate.setDescription("sixth gift card");
        certificate.setPrice(new BigDecimal("23.30"));
        certificate.setDuration(3);
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag(1, "first tag"));
        certificate.setTags(tags);
        sortTypes = new ArrayList<>();
    }

    @Test
    public void save() {
        GiftCertificate savedCertificate = dao.save(certificate);
        assertEquals(savedCertificate, dao.getById(6));
        certificate.setName(null);
        assertThrows(PersistenceException.class, () -> dao.save(certificate));
        certificate.setName("first");
        assertThrows(PersistenceException.class, () -> dao.save(certificate));
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
        assertThrows(NoResultException.class, () -> dao.getByName("no name"));
    }

    @Test
    public void getAll() {
        assertEquals(5, dao.getAll().size());
    }

    @Test
    public void getById() {
        assertEquals(5, dao.getAll().size());
        assertThrows(NullPointerException.class, () -> dao.getById(0));
    }

    @Test
    public void getByTagName() {
        assertEquals(1, dao.getByTagName(sortTypes, Collections.singleton("second tag")).size());
        assertEquals(5, dao.getByTagName(sortTypes, Collections.singleton("first tag")).size());
    }

    @Test
    public void searchByTagAndPartNameOrDescription() {
        assertEquals(4, dao.searchByTagAndPartNameOrDescription(sortTypes,
                Collections.singleton("second tag"), "th").size());
    }

    @Test
    public void getByPartNameOrDescriptionTest() {
        assertEquals(2, dao.searchByPartNameOrDescription(sortTypes, "ir").size());
    }

    @Test
    public void update() {
        Set<Tag> updatableTags = new HashSet<>();
        updatableTags.add(new Tag(2, "second tag"));
        updatableTags.add(new Tag("third tag"));
        updatableTags.add(new Tag("fourth tag"));
        certificate.setId(1);
        certificate.setTags(updatableTags);
        GiftCertificate testableCertificate = dao.update(certificate);
        certificate.setName("second");

        assertEquals(4, dao.getByTagName(sortTypes, Collections.singleton("first tag")).size());
        assertEquals(1, dao.getByTagName(sortTypes, Collections.singleton("third tag")).size());
        assertEquals(1, dao.getByTagName(sortTypes, Collections.singleton("fourth tag")).size());
        assertEquals(1, testableCertificate.getId());
        assertEquals("sixth", testableCertificate.getName());
        assertEquals("sixth gift card", testableCertificate.getDescription());
        assertEquals(new BigDecimal("23.30"), testableCertificate.getPrice());
        assertEquals(3, testableCertificate.getDuration());
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                testableCertificate.getCreateDate().format(DateTimeFormatter.ISO_DATE));
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                testableCertificate.getLastUpdateDate().format(DateTimeFormatter.ISO_DATE));
        assertThrows(NoResultException.class, () -> dao.getByName("first"));
        assertThrows(DataIntegrityViolationException.class, () -> dao.update(certificate));
    }

    @Test
    public void delete() {
        certificate.setId(1);
        dao.delete(certificate);
        assertEquals(4, dao.getByTagName(sortTypes, Collections.singleton("first tag")).size());
    }

    @Test
    public void sortByNameAndDate() {
        List<GiftCertificate> certificates = dao.getByTagName(sortTypes, Collections.singleton("first tag"));
        sortTypes.add("createDate");
        sortTypes.add("name");
        certificates.sort(DATE_COMPARATOR.thenComparing(NAME_COMPARATOR));
        assertEquals(certificates, dao.getByTagName(sortTypes, Collections.singleton("first tag")));
    }

    @Test
    public void sortByName() {
        List<GiftCertificate> certificates = dao.getByTagName(sortTypes, Collections.singleton("first tag"));
        sortTypes.add("name");
        certificates.sort(NAME_COMPARATOR);
        assertEquals(certificates, dao.getByTagName(sortTypes, Collections.singleton("first tag")));
    }

    @Test
    public void sortByDate() {
        List<GiftCertificate> certificates = dao.getByTagName(sortTypes, Collections.singleton("first tag"));
        sortTypes.add("createDate");
        certificates.sort(DATE_COMPARATOR);
        assertEquals(certificates, dao.getByTagName(sortTypes, Collections.singleton("first tag")));
    }
}
