package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Repository
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired @Qualifier("tag_giftCertificate") private SimpleJdbcInsert simpleJdbcInsert;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(GiftCertificate certificate, List<Tag> tags) throws DataIntegrityViolationException {
        saveGiftCertificate(certificate);
        saveTags(certificate, tags);
    }

    private void saveGiftCertificate(GiftCertificate certificate) {
        namedParameterJdbcTemplate.update(
                "INSERT INTO giftCertificate (name, description, price, duration) " +
                        "VALUES (:name, :description, :price, :duration)",
                new BeanPropertySqlParameterSource(certificate));
    }

    private void saveTags(GiftCertificate certificate, List<Tag> tags) {
        tags.forEach(
                tag -> {
                    Map<String, Object> parameter = new HashMap<>();
                    parameter.put("tag_name", tag.getName());
                    parameter.put("giftCertificate_name", certificate.getName());
                    simpleJdbcInsert.execute(parameter);
                });
    }

    @Override
    public GiftCertificate get(String certificateName) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM giftCertificate WHERE name = :name",
                new MapSqlParameterSource(Collections.singletonMap("name", certificateName)),
                mapGiftCertificate());
    }

    private RowMapper<GiftCertificate> mapGiftCertificate() {
        return (rs, rowNum) -> new GiftCertificate(
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getTimestamp("createDate").toInstant(),
                rs.getTimestamp("lastUpdateDate").toInstant(),
                rs.getInt("duration"));
    }

    @Override
    public List<GiftCertificate> getByTagName(String tagName) {
        return jdbcTemplate.query(
                getSqlQueryGettingCertificatesFromDatabase(tagName),
                mapGiftCertificate());
    }

    private String getSqlQueryGettingCertificatesFromDatabase(String tagName) {
        return "SELECT gc.name, gc.description, gc.price, gc.createDate, gc.lastUpdateDate, gc.duration " +
                "FROM giftCertificate gc JOIN tag_giftCertificate tgc " +
                "ON gc.name = tgc.giftCertificate_name " +
                "WHERE tgc.tag_name = '" + tagName + "'";
    }

    @Override
    public List<GiftCertificate> getByPartName(String partName) {
        return jdbcTemplate.query(
                "SELECT * FROM giftCertificate WHERE name LIKE '%" + partName + "%' " +
                        "OR description LIKE '%" + partName + "%'",
                mapGiftCertificate());
    }

    @Override
    public void update(GiftCertificate certificate, String[] fields, String updatableName) {
        namedParameterJdbcTemplate.update(
                getUpdatableSqlQuery(fields, updatableName),
                new BeanPropertySqlParameterSource(certificate));
    }

    private String getUpdatableSqlQuery(String[] fields, String updatableName) {
        return "UPDATE giftCertificate " +
                "SET " + getUpdatableParameters(fields) +
                ", lastUpdateDate = CURRENT_TIMESTAMP " +
                "WHERE name = '" + updatableName + "';";
    }

    private String getUpdatableParameters(String[] fields) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(fields)
                .forEach(field -> sb.append(field).append("=").append(":").append(field).append(" "));
        return formatUpdatableParameters(sb);
    }

    private String formatUpdatableParameters(StringBuilder stringBuilder) {
        return String.join(", ", stringBuilder.toString().split(" "));
    }

    @Override
    public void delete(GiftCertificate certificate) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM giftCertificate WHERE name = :name",
                new BeanPropertySqlParameterSource(certificate));
    }
}
