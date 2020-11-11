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

    static String SAVE_CERTIFICATE =
            "INSERT INTO gift_certificate (name, description, price, duration) " +
                    "VALUES (:name, :description, :price, :duration);";
    static String GET_ALL_CERTIFICATES = "SELECT * FROM gift_certificate;";
    static String GET_CERTIFICATE_BY_ID = "SELECT * FROM gift_certificate WHERE id = :param;";
    static String GET_CERTIFICATE_BY_NAME = "SELECT * FROM gift_certificate WHERE name = :param;";
    static String GET_CERTIFICATES_BY_TAG_NAME =
            "SELECT gc.id, gc.name, gc.description, gc.price, " +
                    "gc.create_date, gc.last_update_date, gc.duration " +
                    "FROM gift_certificate gc " +
                    "JOIN tag_gift_certificate tgc ON gc.id = tgc.gift_certificate_id " +
                    "JOIN tag ON tag.id = tgc.tag_id " +
                    "WHERE tag.name= :param " +
                    "${value};";
    static String GET_CERTIFICATES_BY_PART_NAME_OR_DESCRIPTION =
            "SELECT * FROM gift_certificate gc WHERE name LIKE :param " +
                    "OR description LIKE :param " +
                    "${value};";
    static String GET_CERTIFICATE_BY_TAG_NAME_AND_PART_OF_NAME_OR_DESCRIPTION =
            "SELECT gc.id, gc.name, gc.description, gc.price, " +
                    "gc.create_date, gc.last_update_date, gc.duration " +
                    "FROM gift_certificate gc " +
                    "JOIN tag_gift_certificate tgc ON gc.id = tgc.gift_certificate_id " +
                    "JOIN tag ON tag.id = tgc.tag_id " +
                    "WHERE tag.name= :param " +
                    "OR gc.name LIKE :text " +
                    "OR gc.description LIKE :text " +
                    "${value}";
    static String UPDATE_CERTIFICATE =
            "UPDATE gift_certificate " +
                    "SET ${value}, " +
                    "last_update_date = CURRENT_TIMESTAMP " +
                    "WHERE id = :id;";
    static String DELETE_REFERENCES_BETWEEN_CERTIFICATES_AND_TAGS = "DELETE FROM tag_gift_certificate " +
            "WHERE gift_certificate_id= :id;";
    static String DELETE_CERTIFICATE = "DELETE FROM gift_certificate WHERE id = :id;";
    static RowMapper<GiftCertificate> mapper = (rs, rowNum) -> new GiftCertificate(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getBigDecimal("price"),
            rs.getTimestamp("create_date").toLocalDateTime().atZone(ZoneId.of("GMT+3")),
            rs.getTimestamp("last_update_date").toLocalDateTime().atZone(ZoneId.of("GMT+3")),
            rs.getInt("duration"));

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
        return getAllEntityFromTable(GET_ALL_CERTIFICATES, mapper);
    }

    @Override
    public GiftCertificate getById(long id) {
        return getEntityFromTable(GET_CERTIFICATE_BY_ID, id, mapper);
    }

    @Override
    public GiftCertificate getByName(String name) {
        return getEntityFromTable(GET_CERTIFICATE_BY_NAME, name, mapper);
    }

    @Override
    public List<GiftCertificate> getByTagName(SortCertificatesType type, String name) {
        return getEntityListFromTable(substituteSqlQueryVariable(type.getSortType(), GET_CERTIFICATES_BY_TAG_NAME),
                getParameterMap(type.getSortType(), name, null), mapper);
    }

    private String substituteSqlQueryVariable(String value, String source) {
        return new StringSubstitutor(Collections.singletonMap("value", value)).replace(source);
    }

    @Override
    public List<GiftCertificate> searchByPartNameOrDescription(SortCertificatesType type, String partNameOrDescription) {
        return getEntityListFromTable(substituteSqlQueryVariable(type.getSortType(), GET_CERTIFICATES_BY_PART_NAME_OR_DESCRIPTION),
                getParameterMap(type.getSortType(), prepareParameterForInsertingToSqlScript(partNameOrDescription), null),
                mapper);
    }

    @Override
    public List<GiftCertificate> searchByTagAndPartNameOrDescription(SortCertificatesType type, String tagName, String text) {
        return getEntityListFromTable(
                substituteSqlQueryVariable(type.getSortType(), GET_CERTIFICATE_BY_TAG_NAME_AND_PART_OF_NAME_OR_DESCRIPTION),
                getParameterMap(type.getSortType(), tagName, prepareParameterForInsertingToSqlScript(text)), mapper);
    }

    private Map<String, String> getParameterMap(String sort, String param, @Nullable String text) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("sort", sort);
        parameters.put("param", param);
        parameters.put("text", text);
        return parameters;
    }

    private String prepareParameterForInsertingToSqlScript(String partNameOrDescription) {
        return "%" + partNameOrDescription + "%";
    }

    @Override
    @Transactional
    public void update(GiftCertificate certificate, String[] fields, @Nullable Set<Tag> tags) {
        updateTable(substituteSqlQueryVariable(getUpdatableParameters(fields), UPDATE_CERTIFICATE), certificate);
        updateReferencesBetweenCertificatesAndTagsIfTagsWasPassForIt(certificate, tags);
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
