package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
abstract class Dao<T> {

    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @NonFinal protected SimpleJdbcInsert simpleJdbcInsert;

    Dao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    List<T> getAllEntityFromTable(String sqlScript, RowMapper<T> mapper) {
        return jdbcTemplate.query(sqlScript, mapper);
    }

    T getEntityFromTable(String sqlScript, Object param, RowMapper<T> rowMapper) {
        return namedParameterJdbcTemplate.queryForObject(
                sqlScript,
                Collections.singletonMap("param", param),
                rowMapper);
    }

    void updateTable(String sqlScript, T t) {
        namedParameterJdbcTemplate.update(sqlScript, new BeanPropertySqlParameterSource(t));
    }

    long updateTableWithIdReturn(String sqlScript, T t) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlScript, new BeanPropertySqlParameterSource(t),
                keyHolder, new String[]{"id"});
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0;
    }

    List<T> getAllEntitiesFromTableReferencedEntity(String sqlScript,
                                                    GiftCertificate certificate,
                                                    RowMapper<T> mapper) {
        return namedParameterJdbcTemplate.query(sqlScript, new BeanPropertySqlParameterSource(certificate), mapper);
    }

    List<T> getEntityListFromTable(String sqlScript, Object param, RowMapper<T> rowMapper) {
        return namedParameterJdbcTemplate.query(
                sqlScript,
                Collections.singletonMap("param", param),
                rowMapper);
    }
}
