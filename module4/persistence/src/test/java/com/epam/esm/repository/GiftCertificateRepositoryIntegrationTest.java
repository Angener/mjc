package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.stream.Collectors;

public class GiftCertificateRepositoryIntegrationTest extends InMemoryDbConfig {
    @Autowired
    private GiftCertificateRepository repository;
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
    @Transactional
    public void saveSetsDatesThroughAudit() {
        GiftCertificate savedCertificate = repository.save(certificate);
        assertEquals(savedCertificate, repository.getOne(6));
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                savedCertificate.getCreateDate().format(DateTimeFormatter.ISO_DATE));
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                savedCertificate.getLastUpdateDate().format(DateTimeFormatter.ISO_DATE));
    }

    @Test
    @Transactional
    public void saveTrowsDataIntegrityViolationException() {
        certificate.setName("first");
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(certificate));
    }

    @Test
    @Transactional
    public void GiftCertificateTemplateMatching() {
        GiftCertificate testableCertificate = repository.getOne(1);
        assertEquals(1, testableCertificate.getId());
        assertEquals("first", testableCertificate.getName());
        assertEquals("first gift card", testableCertificate.getDescription());
        assertEquals(new BigDecimal("123.20"), testableCertificate.getPrice());
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                testableCertificate.getCreateDate().format(DateTimeFormatter.ISO_DATE));
        assertEquals(ZonedDateTime.now(ZoneId.of("GMT+3")).format(DateTimeFormatter.ISO_DATE),
                testableCertificate.getLastUpdateDate().format(DateTimeFormatter.ISO_DATE));
        assertEquals(12, testableCertificate.getDuration());
    }

    @Test
    public void findByTagName() {
        assertEquals(1, repository.findByTagName(Collections.singleton("second tag"),
                1, Pageable.unpaged()).getTotalElements());
        assertEquals(5, repository.findByTagName(Collections.singleton("first tag"),
                1, Pageable.unpaged()).getTotalElements());
    }

    @Test
    public void findByTagAndPartNameOrDescription() {
        assertEquals(4, repository.findByTagAndPartNameOrDescription(Collections.singleton("second tag"),
                1, "th", Pageable.unpaged()).getTotalElements());
    }

    @Test
    public void findDistinctByNameLikeOrDescriptionLike() {
        assertEquals(2, repository.findDistinctByNameLikeOrDescriptionLike("ir", Pageable.unpaged())
                .getTotalElements());
    }

    @Test
    public void sortByNameAndDate() {
        List<GiftCertificate> certificates = repository.findByTagName(Collections.singleton("first tag"),
                1, Pageable.unpaged()).stream()
                .sorted(DATE_COMPARATOR.thenComparing(NAME_COMPARATOR))
                .collect(Collectors.toList());
        assertEquals(certificates, repository.findByTagName(Collections.singleton("first tag"), 1,
                PageRequest.of(0, 5, Sort.by("createDate", "name"))).toList());
    }

    @Test
    public void sortByName() {
        List<GiftCertificate> certificates = repository.findByTagName(Collections.singleton("first tag"),
                1, Pageable.unpaged()).stream()
                .sorted(NAME_COMPARATOR)
                .collect(Collectors.toList());
        assertEquals(certificates, repository.findByTagName(Collections.singleton("first tag"), 1,
                PageRequest.of(0, 5, Sort.by("name"))).toList());
    }
}
