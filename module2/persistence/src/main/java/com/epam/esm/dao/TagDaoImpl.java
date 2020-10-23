package com.epam.esm.dao;


import com.epam.esm.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class TagDaoImpl implements TagDao {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Override
    public List<Tag> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM tag",
                (rs, mapRow) -> new Tag(rs.getString("name")));
    }

    @Override
    public Tag get(String name) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM tag WHERE name = :name",
                new MapSqlParameterSource(Collections.singletonMap("name", name)),
                (rs, mapRow) -> new Tag(rs.getString("name")));
    }

    @Override
    public void save(Tag tag) {
        Map<String, Object> parameter = Collections.singletonMap("name", tag.getName());
        simpleJdbcInsert.withTableName("tag").execute(parameter);
    }

    @Override
    public void delete(Tag tag) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM tag WHERE name = :name",
                new BeanPropertySqlParameterSource(tag));
    }
}
