package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class DatabaseResolver<T> {

    JdbcTemplate jdbcTemplate;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public DatabaseResolver(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
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

    List<T> getEntityListFromTable(String sqlScript, Map<String, String> param, RowMapper<T> rowMapper) {
        return namedParameterJdbcTemplate.query(sqlScript, param, rowMapper);
    }
}
