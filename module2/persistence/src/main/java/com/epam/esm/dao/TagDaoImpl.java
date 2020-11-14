package com.epam.esm.dao;


import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagDaoImpl implements TagDao {
    static String GET_ALL_TAGS = "SELECT * FROM tag;";
    static String GET_TAG_BY_ID = "SELECT * FROM tag WHERE id = :param;";
    static String GET_TAG_BY_NAME = "SELECT * FROM tag WHERE name = :param;";
    static String GET_ALL_CERTIFICATE_TAGS =
            "SELECT tag.id, tag.name FROM tag " +
                    "JOIN tag_gift_certificate tgc ON tag.id = tgc.tag_id " +
                    "JOIN gift_certificate ON gift_certificate.id = tgc.gift_certificate_id " +
                    "WHERE gift_certificate.id = :id;";
    static String SAVE_TAG = "INSERT INTO tag (name) VALUES (:name);";
    static String DELETE_TAG = "DELETE FROM tag WHERE id = :id;";
    static RowMapper<Tag> mapper = (rs, mapRow) -> new Tag(rs.getLong("id"),
            rs.getString("name"));
    DaoHelper daoHelper;

    @Autowired
    public TagDaoImpl(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    @Override
    public List<Tag> getAll() {
        return daoHelper.getAllEntityFromTable(GET_ALL_TAGS, mapper);
    }

    @Override
    public Tag getById(long id) {
        return daoHelper.getEntityFromTable(GET_TAG_BY_ID, id, mapper);
    }

    @Override
    public Tag getByName(String name) {
        return daoHelper.getEntityFromTable(GET_TAG_BY_NAME, name, mapper);
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate certificate) {
        return daoHelper.getAllEntitiesFromTableReferencedEntity(GET_ALL_CERTIFICATE_TAGS, certificate, mapper);
    }

    @Override
    @Transactional
    public Tag save(Tag tag) {
        return getById(daoHelper.updateTableWithIdReturn(SAVE_TAG, tag));
    }

    @Override
    public void delete(Tag tag) {
        daoHelper.updateTable(DELETE_TAG, tag);
    }
}
