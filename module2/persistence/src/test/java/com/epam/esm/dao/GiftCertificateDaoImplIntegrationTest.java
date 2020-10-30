package com.epam.esm.dao;

import com.epam.esm.config.DbUnitConfig;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
public class GiftCertificateDaoImplIntegrationTest extends DbUnitConfig {
    @Autowired
    private GiftCertificateDao giftCertificateDao;
    private GiftCertificate giftCertificate;
    private List<Tag> tags;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        beforeData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("giftCertificateDataset.xml"));
        tester.setDataSet(beforeData);
        tester.onSetup();
        initFields();

    }

    private void initFields() {
        giftCertificate = new GiftCertificate();
        giftCertificate.setName("name");
        giftCertificate.setDescription("description");
        giftCertificate.setPrice(new BigDecimal("23.2"));
        giftCertificate.setDuration(3);
        tags = Collections.singletonList(new Tag(1, "first tag"));
    }

    @Test
    public void compareWithTableTest() throws Exception {
        IDataSet databaseDataSet = getConnection().createDataSet();
        IDataSet expectedDataSet = tester.getConnection().createDataSet();

        String[] ignore = {"createDate", "lastUpdateDate"};
        Assertion.assertEqualsIgnoreCols(databaseDataSet, expectedDataSet, "giftCertificate", ignore);
    }

    @Test
    public void saveTest() {
        giftCertificateDao.save(giftCertificate, tags);
        GiftCertificate emptyGiftCertificate = new GiftCertificate();
        List<Tag> emptyList = new ArrayList<>();

        assertEquals(giftCertificateDao.get("name").getName(), giftCertificate.getName());
        assertThrows(IllegalArgumentException.class, () -> giftCertificateDao.save(null, tags));
        assertThrows(DuplicateKeyException.class, () -> giftCertificateDao.save(giftCertificate, null));
        assertThrows(DataIntegrityViolationException.class, () -> giftCertificateDao.save(emptyGiftCertificate, tags));
        emptyGiftCertificate.setName("name");
        assertThrows(DuplicateKeyException.class, () -> giftCertificateDao.save(giftCertificate, emptyList));
    }

    @Test
    public void getTest() {
        String name = "first";
        GiftCertificate giftCertificate = giftCertificateDao.get(name);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        assertEquals("first", giftCertificate.getName());
        assertEquals("first gift card", giftCertificate.getDescription());
        assertEquals(new BigDecimal("123.20"), giftCertificate.getPrice());
        assertEquals("10/30/2020",
                giftCertificate.getCreateDate().atZone(ZoneId.of("GMT+3")).format(formatter));
        assertEquals("10/30/2020",
                giftCertificate.getLastUpdateDate().atZone(ZoneId.of("GMT+3")).format(formatter));
        assertEquals(12, giftCertificate.getDuration());
        assertThrows(EmptyResultDataAccessException.class, () -> giftCertificateDao.get(null));
    }

    @Test
    public void getByTagNameTest() {
        assertEquals(1, giftCertificateDao.getByTagName("second tag").size());
    }

    @Test
    public void getByPartNameOrDescriptionTest() {
        assertEquals(2, giftCertificateDao.getByPartName("ir").size());
    }

    @Test
    public void updateTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String[] fields = {"name", "description", "price", "duration"};
        GiftCertificate updatableCertificate = new GiftCertificate();
        updatableCertificate.setId(5);
        updatableCertificate.setName("sixth");
        updatableCertificate.setDescription("sixth gift card");
        updatableCertificate.setPrice(new BigDecimal("123123123.09"));
        updatableCertificate.setDuration(223);
        giftCertificateDao.update(updatableCertificate, fields);
        GiftCertificate testCertificate = giftCertificateDao.get("sixth");

        assertEquals(5, giftCertificateDao.getByTagName("first tag").size());
        assertEquals("sixth", testCertificate.getName());
        assertEquals("sixth gift card", testCertificate.getDescription());
        assertEquals(new BigDecimal("123123123.09"), testCertificate.getPrice());
        assertEquals("10/30/2020",
                testCertificate.getCreateDate().atZone(ZoneId.of("GMT+3")).format(formatter));
        assertEquals(Instant.now().atZone(ZoneId.of("GMT+3")).format(formatter),
                testCertificate.getLastUpdateDate().atZone(ZoneId.of("GMT+3")).format(formatter));
        assertEquals(223, testCertificate.getDuration());
    }

    @Test
    public void updateThrowsNpe() {
        String[] fields = {"name"};
        GiftCertificate updatableCertificate = new GiftCertificate();
        updatableCertificate.setId(5);
        updatableCertificate.setName("first");

        assertThrows(DuplicateKeyException.class,
                () -> giftCertificateDao.update(updatableCertificate, fields));

        updatableCertificate.setName("sixth");
        fields[0] = "awd";
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> giftCertificateDao.update(updatableCertificate, fields));
    }

    @Test
    public void deleteTest() {
        GiftCertificate testCertificate = new GiftCertificate();
        testCertificate.setId(1);
        giftCertificateDao.delete(testCertificate);
        assertEquals(4, giftCertificateDao.getByTagName("first tag").size());
        assertThrows(IllegalArgumentException.class, () -> giftCertificateDao.delete(null));
    }
}
