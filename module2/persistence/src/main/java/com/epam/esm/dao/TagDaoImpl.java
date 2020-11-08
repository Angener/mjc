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

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public List<Tag> getAll() {
        return getAllEntityFromTable(SqlScript.GET_ALL_TAGS.getScript(), getTagRowMap());
    }

    private RowMapper<Tag> getTagRowMap() {
        return (rs, mapRow) -> new Tag(
                rs.getLong("id"),
                rs.getString("name"));
    }

    @Override
    public Tag getById(long id) {
        return getEntityFromTable(SqlScript.GET_TAG_BY_ID.getScript(), id, getTagRowMap());
    }

    @Override
    public Tag getByName(String name) {
        return getEntityFromTable(SqlScript.GET_TAG_BY_NAME.getScript(), name, getTagRowMap());
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate certificate) {
        return getAllEntitiesFromTableReferencedEntity(
                SqlScript.GET_ALL_CERTIFICATE_TAGS.getScript(),
                certificate,
                getTagRowMap());
    }

    @Override
    public long save(Tag tag) {
       return updateTableWithIdReturn(SqlScript.SAVE_TAG.getScript(), tag);
    }

    @Override
    public void delete(Tag tag) {
        updateTable(SqlScript.DELETE_TAG.getScript(), tag);
    }
}
