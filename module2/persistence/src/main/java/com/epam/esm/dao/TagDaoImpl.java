package com.epam.esm.dao;


import com.epam.esm.entity.Tag;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Repository
public class TagDaoImpl implements TagDao {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired @Qualifier(value = "tag") private SimpleJdbcInsert simpleJdbcInsert;

    @Override
    public List<Tag> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM tag",
                (rs, mapRow) -> new Tag(rs.getLong("id"), rs.getString("name")));
    }

    @Override
    public Tag get(String name) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM tag WHERE name = :name",
                Collections.singletonMap("name", name),
                (rs, mapRow) -> new Tag(rs.getLong("id"), rs.getString("name")));
    }

    @Override
    public void save(Tag tag) {
        namedParameterJdbcTemplate.update("INSERT INTO tag (name) VALUES (:name)",
                new BeanPropertySqlParameterSource(tag));
    }

    @Override
    public void delete(Tag tag) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM tag WHERE id = :id",
                new BeanPropertySqlParameterSource(tag));
    }
}
