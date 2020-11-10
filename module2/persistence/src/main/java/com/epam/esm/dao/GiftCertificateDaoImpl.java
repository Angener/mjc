package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
import java.util.Set;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiftCertificateDaoImpl extends Dao<GiftCertificate> implements GiftCertificateDao {

    TagDao tagDao;
    SimpleJdbcInsert simpleJdbcInsert;

    private static final String SAVE_CERTIFICATE =
            "INSERT INTO gift_certificate (name, description, price, duration) " +
                    "VALUES (:name, :description, :price, :duration);";
    private static final String GET_ALL_CERTIFICATES = "SELECT * FROM gift_certificate;";
    private static final String GET_CERTIFICATE_BY_ID = "SELECT * FROM gift_certificate WHERE id = :param;";
    private static final String GET_CERTIFICATE_BY_NAME = "SELECT * FROM gift_certificate WHERE name = :param;";
    private static final String GET_CERTIFICATES_BY_TAG_NAME =
            "SELECT gc.id, gc.name, gc.description, gc.price, " +
                    "gc.create_date, gc.last_update_date, gc.duration " +
                    "FROM gift_certificate gc " +
                    "JOIN tag_gift_certificate tgc ON gc.id = tgc.gift_certificate_id " +
                    "JOIN tag ON tag.id = tgc.tag_id " +
                    "WHERE tag.name= :param;";
    private static final String GET_CERTIFICATES_BY_PART_NAME_OR_DESCRIPTION =
            "SELECT * FROM gift_certificate WHERE name LIKE :param " +
                    "OR description LIKE :param;";
    private static final String UPDATE_CERTIFICATE =
            "UPDATE gift_certificate " +
                    "SET ${values}, " +
                    "last_update_date = CURRENT_TIMESTAMP " +
                    "WHERE id = :id;";
    private static final String DELETE_REFERENCES_BETWEEN_CERTIFICATES_AND_TAGS = "DELETE FROM tag_gift_certificate WHERE gift_certificate_id= :id;";
    private static final String DELETE_CERTIFICATE = "DELETE FROM gift_certificate WHERE id = :id;";

    @Autowired
    public GiftCertificateDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                  SimpleJdbcInsert simpleJdbcInsert,
                                  JdbcTemplate jdbcTemplate,
                                  TagDao tagDao) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
        this.tagDao = tagDao;
        this.simpleJdbcInsert = simpleJdbcInsert;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long save(GiftCertificate certificate, Set<Tag> tags) {
        long id;
        saveTags(tags);
        id = updateTableWithIdReturn(SAVE_CERTIFICATE, certificate);
        saveReferencesBetweenCertificatesAndTags(id, tags);
        return id;
    }

    private void saveTags(Set<Tag> tags) {
        tags.stream()
                .filter(tag -> tag.getId() == 0)
                .forEach(tag -> tag.setId(tagDao.save(tag)));
    }

    private void saveReferencesBetweenCertificatesAndTags(long certificateId, Set<Tag> tags) {
        tags.forEach(
                tag -> {
                    Map<String, Object> parameter = new HashMap<>();
                    parameter.put("tag_id", tag.getId());
                    parameter.put("gift_certificate_id", certificateId);
                    simpleJdbcInsert.execute(parameter);
                });
    }

    @Override
    public List<GiftCertificate> getAll() {
        return getAllEntityFromTable(GET_ALL_CERTIFICATES, getGiftCertificateRowMap());
    }

    @Override
    public GiftCertificate getById(long id) {
        return getEntityFromTable(GET_CERTIFICATE_BY_ID, id, getGiftCertificateRowMap());
    }

    @Override
    public GiftCertificate getByName(String name) {
        return getEntityFromTable(GET_CERTIFICATE_BY_NAME, name, getGiftCertificateRowMap());
    }

    private RowMapper<GiftCertificate> getGiftCertificateRowMap() {
        return (rs, rowNum) -> new GiftCertificate(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getTimestamp("create_date").toLocalDateTime().atZone(ZoneId.of("GMT+3")),
                rs.getTimestamp("last_update_date").toLocalDateTime().atZone(ZoneId.of("GMT+3")),
                rs.getInt("duration"));
    }

    @Override
    public List<GiftCertificate> getByTagName(String name) {
        return getEntityListFromTable(GET_CERTIFICATES_BY_TAG_NAME, name, getGiftCertificateRowMap());
    }

    @Override
    public List<GiftCertificate> searchByPartNameOrDescription(String partNameOrDescription) {
        return getEntityListFromTable(
                GET_CERTIFICATES_BY_PART_NAME_OR_DESCRIPTION,
                prepareParameterForInsertingToSqlScript(partNameOrDescription),
                getGiftCertificateRowMap());
    }

    private String prepareParameterForInsertingToSqlScript(String partNameOrDescription) {
        return "%" + partNameOrDescription + "%";
    }

    @Override
    @Transactional
    public void update(GiftCertificate certificate, String[] fields, @Nullable Set<Tag> tags) {
        updateTable(getUpdatingSqlScript(fields), certificate);
        updateReferencesBetweenCertificatesAndTagsIfTagsWasPassForIt(certificate, tags);
    }

    private String getUpdatingSqlScript(String[] fields) {
        return new StringSubstitutor(Collections.singletonMap("values", getUpdatableParameters(fields)))
                .replace(UPDATE_CERTIFICATE);
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
                                                                              Set<Tag> tags) {
        if (isTagsPassedForUpdate(tags)) {
            updateReferencesBetweenCertificatesAndTags(certificate, tags);
        }
    }

    private boolean isTagsPassedForUpdate(Set<Tag> tags) {
        return tags.size() > 0;
    }

    private void updateReferencesBetweenCertificatesAndTags(GiftCertificate certificate, Set<Tag> tags) {
        saveTags(tags);
        updateTable(DELETE_REFERENCES_BETWEEN_CERTIFICATES_AND_TAGS, certificate);
        saveReferencesBetweenCertificatesAndTags(certificate.getId(), tags);
    }

    @Override
    public void delete(GiftCertificate certificate) {
        updateTable(DELETE_CERTIFICATE, certificate);
    }
}
