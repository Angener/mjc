package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.lang.Nullable;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired @Qualifier("tag_giftCertificate") SimpleJdbcInsert simpleJdbcInsert;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(GiftCertificate certificate, List<Tag> tags) {
        saveGiftCertificate(certificate);
        saveReferencesBetweenCertificatesAndTags(get(certificate.getName()), tags);
    }

    private void saveGiftCertificate(GiftCertificate certificate) {
        namedParameterJdbcTemplate.update(
                "INSERT INTO giftCertificate (name, description, price, duration) " +
                        "VALUES (:name, :description, :price, :duration)",
                new BeanPropertySqlParameterSource(certificate));
    }

    private void saveReferencesBetweenCertificatesAndTags(GiftCertificate certificate, List<Tag> tags) {
        tags.forEach(
                tag -> {
                    Map<String, Object> parameter = new HashMap<>();
                    parameter.put("tag_id", tag.getId());
                    parameter.put("giftCertificate_id", certificate.getId());
                    simpleJdbcInsert.execute(parameter);
                });
    }

    @Override
    public GiftCertificate get(String certificateName) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM giftCertificate WHERE name = :name",
                Collections.singletonMap("name", certificateName),
                getGiftCertificateRowMap());
    }

    private RowMapper<GiftCertificate> getGiftCertificateRowMap() {
        return (rs, rowNum) -> new GiftCertificate(
                rs.getLong("id"),
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
                getSqlScriptGettingCertificatesFromDatabase(tagName),
                getGiftCertificateRowMap());
    }

    private String getSqlScriptGettingCertificatesFromDatabase(String tagName) {
        return "SELECT gc.id, gc.name, gc.description, gc.price, " +
                "gc.createDate, gc.lastUpdateDate, gc.duration " +
                "FROM giftCertificate gc " +
                "JOIN tag_giftCertificate tgc ON gc.id = tgc.giftCertificate_id " +
                "JOIN tag ON tag.id = tgc.tag_id " +
                "WHERE tag.name='" + tagName + "';";
    }

    @Override
    public List<GiftCertificate> getByPartName(String partNameOrDescription) {
        return jdbcTemplate.query(
                getSqlScriptGettingTagsByPartNameOrDescription(partNameOrDescription),
                getGiftCertificateRowMap());
    }

    private String getSqlScriptGettingTagsByPartNameOrDescription(String partNameOrDescription) {
        return "SELECT * FROM giftCertificate WHERE name LIKE '%" + partNameOrDescription + "%' " +
                "OR description LIKE '%" + partNameOrDescription + "%'";
    }

    @Override
    @Transactional
    public void update(GiftCertificate certificate, String[] fields, @Nullable List<Tag> tags) {
        updateGiftCertificate(certificate, fields);
        updateReferencesBetweenCertificatesAndTagsIfTagsWasPassForIt(certificate, tags);
    }

    private void updateGiftCertificate(GiftCertificate certificate, String[] fields) {
        namedParameterJdbcTemplate.update(
                getUpdatingSqlScript(fields, certificate),
                new BeanPropertySqlParameterSource(certificate));
    }

    private String getUpdatingSqlScript(String[] fields, GiftCertificate certificate) {
        return "UPDATE giftCertificate " +
                "SET " + getUpdatableParameters(fields) +
                ", lastUpdateDate = CURRENT_TIMESTAMP " +
                "WHERE id = '" + certificate.getId() + "';";
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

    private void updateReferencesBetweenCertificatesAndTagsIfTagsWasPassForIt(GiftCertificate certificate,
                                                                              List<Tag> tags) {
        if (isTagsPassedForUpdate(tags)) {
            updateReferencesBetweenCertificatesAndTags(certificate, tags);
        }
    }

    private boolean isTagsPassedForUpdate(List<Tag> tags) {
        return tags.size() > 0;
    }

    private void updateReferencesBetweenCertificatesAndTags(GiftCertificate certificate, List<Tag> tags) {
        removeExistReferences(certificate);
        saveReferencesBetweenCertificatesAndTags(certificate, tags);
    }

    private void removeExistReferences(GiftCertificate certificate) {
        jdbcTemplate.execute(getSqlScriptRemovingExistReferencesBetweenCertificatesAndTags(certificate));
    }

    private String getSqlScriptRemovingExistReferencesBetweenCertificatesAndTags(GiftCertificate certificate) {
        return "DELETE FROM tag_giftCertificate WHERE giftCertificate_id=" + certificate.getId() + ";";
    }

    @Override
    public void delete(GiftCertificate certificate) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM giftCertificate WHERE id = :id",
                new BeanPropertySqlParameterSource(certificate));
    }
}
