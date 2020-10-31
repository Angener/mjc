package com.epam.esm.dao;


import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagDaoImpl implements TagDao {

    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Tag> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM tag",
                getTagRowMap());
    }

    private RowMapper<Tag> getTagRowMap(){
        return (rs, mapRow) -> new Tag(
                rs.getLong("id"),
                rs.getString("name"));
    }

    @Override
    public Tag get(String name) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM tag WHERE name = :name",
                Collections.singletonMap("name", name),
                getTagRowMap());
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate giftCertificate){
        return jdbcTemplate.query(
                getSqlQueryGettingAllGiftCertificateTags(giftCertificate),
                getTagRowMap());
    }

    private String getSqlQueryGettingAllGiftCertificateTags(GiftCertificate certificate) {
        return "SELECT tag.id, tag.name FROM tag " +
                "JOIN tag_giftCertificate tgc ON tag.id = tgc.tag_id " +
                "JOIN giftCertificate ON giftCertificate.id = tgc.giftCertificate_id " +
                "WHERE giftCertificate.id=" + certificate.getId() + ";";
    }

    @Override
    public void save(Tag tag) {
        namedParameterJdbcTemplate.update("INSERT INTO tag (name) VALUES (:name) ON CONFLICT DO NOTHING",
                new BeanPropertySqlParameterSource(tag));
    }

    @Override
    public void delete(Tag tag) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM tag WHERE id = :id",
                new BeanPropertySqlParameterSource(tag));
    }
}
