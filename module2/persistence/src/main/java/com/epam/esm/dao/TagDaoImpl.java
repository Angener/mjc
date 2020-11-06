package com.epam.esm.dao;


import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TagDaoImpl implements TagDao {

    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Tag> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM tag;",
                getTagRowMap());
    }

    private RowMapper<Tag> getTagRowMap() {
        return (rs, mapRow) -> new Tag(
                rs.getLong("id"),
                rs.getString("name"));
    }
    //TODO cover with test <==================================================================
    @Override
    public Tag getById(long id){
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM tag WHERE id = :id;",
                Collections.singletonMap("id", id),
                getTagRowMap());
    }

    @Override
    public Tag get(String name) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM tag WHERE name = :name;",
                Collections.singletonMap("name", name),
                getTagRowMap());
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate giftCertificate) {
        return namedParameterJdbcTemplate.query(
                getSqlScriptGettingAllGiftCertificateTags(),
                new BeanPropertySqlParameterSource(giftCertificate),
                getTagRowMap()
        );
    }

    private String getSqlScriptGettingAllGiftCertificateTags() {
        return "SELECT tag.id, tag.name FROM tag " +
                "JOIN tag_gift_certificate tgc ON tag.id = tgc.tag_id " +
                "JOIN gift_certificate ON gift_certificate.id = tgc.gift_certificate_id " +
                "WHERE gift_certificate.id = :id;";
    }

    @Override
    public void save(Tag tag) {
        namedParameterJdbcTemplate.update("INSERT INTO tag (name) VALUES (:name) ON CONFLICT DO NOTHING;",
                new BeanPropertySqlParameterSource(tag));
    }

    @Override
    public void delete(Tag tag) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM tag WHERE id = :id;",
                new BeanPropertySqlParameterSource(tag));
    }
}
