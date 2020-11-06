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
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GiftCertificateDaoImpl implements GiftCertificateDao {

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    SimpleJdbcInsert simpleJdbcInsert;
    JdbcTemplate jdbcTemplate;
    TagDao tagDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(GiftCertificate certificate, List<Tag> tags) {
        saveTags(tags);
        saveGiftCertificate(certificate);
        saveReferencesBetweenCertificatesAndTags(get(certificate.getName()), updateTagsId(tags));
    }

    private void saveTags(List<Tag> tags) {
        tags.forEach(tagDao::save);
    }

    private void saveGiftCertificate(GiftCertificate certificate) {
        namedParameterJdbcTemplate.update(
                "INSERT INTO giftCertificate (name, description, price, duration) " +
                        "VALUES (:name, :description, :price, :duration);",
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

    private List<Tag> updateTagsId(List<Tag> tags) {
        return tags.stream()
                .map(tag -> tagDao.get(tag.getName()))
                .collect(Collectors.toList());
    }

    //TODO cover with test <==================================================================
    @Override
    public List<GiftCertificate> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM giftCertificate;",
                getGiftCertificateRowMap());
    }

    //TODO cover with test <==================================================================
    @Override
    public GiftCertificate getById(long id){
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM giftCertificate WHERE id = :id;",
                Collections.singletonMap("id", id),
                getGiftCertificateRowMap());
    }

    @Override
    public GiftCertificate get(String certificateName) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT * FROM giftCertificate WHERE name = :name;",
                Collections.singletonMap("name", certificateName),
                getGiftCertificateRowMap());
    }

    private RowMapper<GiftCertificate> getGiftCertificateRowMap() {
        return (rs, rowNum) -> new GiftCertificate(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getTimestamp("createDate").toLocalDateTime().atZone(ZoneId.of("GMT+3")),
                rs.getTimestamp("lastUpdateDate").toLocalDateTime().atZone(ZoneId.of("GMT+3")),
                rs.getInt("duration"));
    }

    @Override
    public List<GiftCertificate> getByTagName(String tagName) {
        return namedParameterJdbcTemplate.query(
                getSqlScriptGettingCertificatesFromDatabase(),
                Collections.singletonMap("tagName", tagName),
                getGiftCertificateRowMap());
    }

    private String getSqlScriptGettingCertificatesFromDatabase() {
        return "SELECT gc.id, gc.name, gc.description, gc.price, " +
                "gc.createDate, gc.lastUpdateDate, gc.duration " +
                "FROM giftCertificate gc " +
                "JOIN tag_giftCertificate tgc ON gc.id = tgc.giftCertificate_id " +
                "JOIN tag ON tag.id = tgc.tag_id " +
                "WHERE tag.name= :tagName;";
    }

    @Override
    public List<GiftCertificate> searchByPartNameOrDescription(String partNameOrDescription) {
        return namedParameterJdbcTemplate.query(
                getSqlScriptGettingTagsByPartNameOrDescription(),
                Collections.singletonMap("partNameOrDescription", "%" + partNameOrDescription + "%"),
                getGiftCertificateRowMap());
    }

    private String getSqlScriptGettingTagsByPartNameOrDescription() {
        return "SELECT * FROM giftCertificate WHERE name LIKE :partNameOrDescription " +
                "OR description LIKE :partNameOrDescription;";
    }

    @Override
    @Transactional
    public void update(GiftCertificate certificate, String[] fields, @Nullable List<Tag> tags) {
        updateGiftCertificate(certificate, fields);
        updateReferencesBetweenCertificatesAndTagsIfTagsWasPassForIt(certificate, tags);
    }

    private void updateGiftCertificate(GiftCertificate certificate, String[] fields) {
        namedParameterJdbcTemplate.update(
                getUpdatingSqlScript(fields),
                new BeanPropertySqlParameterSource(certificate));
    }

    private String getUpdatingSqlScript(String[] fields) {
        return "UPDATE giftCertificate " +
                "SET " + getUpdatableParameters(fields) +
                ", lastUpdateDate = CURRENT_TIMESTAMP " +
                "WHERE id = :id;";
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
        saveTags(tags);
        removeExistReferences(certificate);
        saveReferencesBetweenCertificatesAndTags(certificate, updateTagsId(tags));
    }

    private void removeExistReferences(GiftCertificate certificate) {
        namedParameterJdbcTemplate.update(
                getSqlScriptRemovingExistReferencesBetweenCertificatesAndTags(),
                new BeanPropertySqlParameterSource(certificate));
    }

    private String getSqlScriptRemovingExistReferencesBetweenCertificatesAndTags() {
        return "DELETE FROM tag_giftCertificate WHERE giftCertificate_id= :id;";
    }

    @Override
    public void delete(GiftCertificate certificate) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM giftCertificate WHERE id = :id;",
                new BeanPropertySqlParameterSource(certificate));
    }
}
