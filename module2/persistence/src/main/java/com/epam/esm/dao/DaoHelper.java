package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class DaoHelper {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public DaoHelper(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    <T> List<T> getAllEntityFromTable(String sqlScript, RowMapper<T> mapper) {
        return namedParameterJdbcTemplate.query(sqlScript, mapper);
    }

    <T> T getEntityFromTable(String sqlScript, Object param, RowMapper<T> rowMapper) {
        return namedParameterJdbcTemplate.queryForObject(
                sqlScript,
                Collections.singletonMap("param", param),
                rowMapper);
    }

    <T> void updateTable(String sqlScript, T t) {
        namedParameterJdbcTemplate.update(sqlScript, new BeanPropertySqlParameterSource(t));
    }

    <T> long updateTableWithIdReturn(String sqlScript, T bean) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlScript, new BeanPropertySqlParameterSource(bean),
                keyHolder, new String[]{"id"});
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : 0;
    }

    <T> List<T> getAllEntitiesFromTableReferencedEntity(String sqlScript,
                                                    GiftCertificate certificate,
                                                    RowMapper<T> mapper) {
        return namedParameterJdbcTemplate.query(sqlScript, new BeanPropertySqlParameterSource(certificate), mapper);
    }

    <T> List<T> getEntityListFromTable(String sqlScript, Map<String, String> param, RowMapper<T> rowMapper) {
        return namedParameterJdbcTemplate.query(sqlScript, param, rowMapper);
    }
}
