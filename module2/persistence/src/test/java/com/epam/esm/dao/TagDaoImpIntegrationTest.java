package com.epam.esm.dao;

import com.epam.esm.config.DbUnitConfig;
import com.epam.esm.entity.Tag;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TagDaoImpIntegrationTest extends DbUnitConfig {
    @Autowired
    private TagDao tagDao;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        beforeData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("giftCertificateDataset.xml"));
        tester.setDataSet(beforeData);
        tester.onSetup();
    }

    @Test
    public void getAllTest() throws Exception {
        List<Tag> tags = tagDao.getAll();
        IDataSet databaseDataSet = getConnection().createDataSet();
        ITable factTable = databaseDataSet.getTable("tag");
        IDataSet expectedDataSet = tester.getConnection().createDataSet();
        ITable expectedTable = expectedDataSet.getTable("tag");

        Assertion.assertEquals(expectedTable, factTable);
        assertEquals(expectedTable.getRowCount(), tags.size());
    }

    @Test
    public void getTest() {
        Tag tag = tagDao.get("first tag");
        assertEquals("first tag", tag.getName());
        assertThrows(EmptyResultDataAccessException.class, () -> tagDao.get("ninth tag"));
    }

    @Test
    public void saveTest() {
        Tag tag = new Tag();
        tag.setName("third tag");
        tagDao.save(tag);
        Tag tagWithNullName = new Tag();

        assertEquals(tagDao.getAll().size(), 3);
        assertThrows(DataIntegrityViolationException.class, () -> tagDao.save(tagWithNullName));
        tagWithNullName.setName("third tag");
        assertThrows(DuplicateKeyException.class, () -> tagDao.save(tagWithNullName));
    }

    @Test
    public void deleteTest() {
        System.out.println(tagDao.getAll());
        Tag tag = new Tag(2, "second tag");
        tagDao.delete(tag);
        assertEquals(1, tagDao.getAll().size());
    }
}
