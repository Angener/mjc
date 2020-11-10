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
    private static final String GET_ALL_TAGS = "SELECT * FROM tag;";
    private static final String GET_TAG_BY_ID = "SELECT * FROM tag WHERE id = :param;";
    private static final String GET_TAG_BY_NAME = "SELECT * FROM tag WHERE name = :param;";
    private static final String GET_ALL_CERTIFICATE_TAGS =
            "SELECT tag.id, tag.name FROM tag " +
                    "JOIN tag_gift_certificate tgc ON tag.id = tgc.tag_id " +
                    "JOIN gift_certificate ON gift_certificate.id = tgc.gift_certificate_id " +
                    "WHERE gift_certificate.id = :id;";
    private static final String SAVE_TAG = "INSERT INTO tag (name) VALUES (:name) ON CONFLICT DO NOTHING;";
    private static final String DELETE_TAG = "DELETE FROM tag WHERE id = :id;";

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public List<Tag> getAll() {
        return getAllEntityFromTable(GET_ALL_TAGS, getTagRowMap());
    }

    private RowMapper<Tag> getTagRowMap() {
        return (rs, mapRow) -> new Tag(
                rs.getLong("id"),
                rs.getString("name"));
    }

    @Override
    public Tag getById(long id) {
        return getEntityFromTable(GET_TAG_BY_ID, id, getTagRowMap());
    }

    @Override
    public Tag getByName(String name) {
        return getEntityFromTable(GET_TAG_BY_NAME, name, getTagRowMap());
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate certificate) {
        return getAllEntitiesFromTableReferencedEntity(
                GET_ALL_CERTIFICATE_TAGS,
                certificate,
                getTagRowMap());
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
