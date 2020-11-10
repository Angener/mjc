package com.epam.esm.dao;


import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagDaoImpl extends Dao<Tag> implements TagDao {
    static String GET_ALL_TAGS = "SELECT * FROM tag;";
    static String GET_TAG_BY_ID = "SELECT * FROM tag WHERE id = :param;";
    static String GET_TAG_BY_NAME = "SELECT * FROM tag WHERE name = :param;";
    static String GET_ALL_CERTIFICATE_TAGS =
            "SELECT tag.id, tag.name FROM tag " +
                    "JOIN tag_gift_certificate tgc ON tag.id = tgc.tag_id " +
                    "JOIN gift_certificate ON gift_certificate.id = tgc.gift_certificate_id " +
                    "WHERE gift_certificate.id = :id;";
    static String SAVE_TAG = "INSERT INTO tag (name) VALUES (:name) ON CONFLICT DO NOTHING;";
    static String DELETE_TAG = "DELETE FROM tag WHERE id = :id;";
    static RowMapper<Tag> mapper = (rs, mapRow) -> new Tag(rs.getLong("id"),
            rs.getString("name"));

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public List<Tag> getAll() {
        return getAllEntityFromTable(GET_ALL_TAGS, mapper);
    }

    @Override
    public Tag getById(long id) {
        return getEntityFromTable(GET_TAG_BY_ID, id, mapper);
    }

    @Override
    public Tag getByName(String name) {
        return getEntityFromTable(GET_TAG_BY_NAME, name, mapper);
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate certificate) {
        return getAllEntitiesFromTableReferencedEntity(GET_ALL_CERTIFICATE_TAGS, certificate, mapper);
    }

    @Override
    public long save(Tag tag) {
        return updateTableWithIdReturn(SAVE_TAG, tag);
    }

    @Override
    public void delete(Tag tag) {
        updateTable(DELETE_TAG, tag);
    }
}
