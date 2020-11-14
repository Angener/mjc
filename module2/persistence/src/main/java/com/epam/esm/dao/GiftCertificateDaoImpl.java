package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
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
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiftCertificateDaoImpl implements GiftCertificateDao {
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
    static String SORTING_ORDER = " ASC";
    static List<String> SORTABLE_TABLE_FIELDS = Arrays.asList("name", "create_date");

    TagDao tagDao;
    SimpleJdbcInsert simpleJdbcInsert;
    DaoHelper daoHelper;

    @Autowired
    public GiftCertificateDaoImpl(SimpleJdbcInsert simpleJdbcInsert,
                                  TagDao tagDao,
                                  DaoHelper daoHelper) {
        this.tagDao = tagDao;
        this.simpleJdbcInsert = simpleJdbcInsert;
        this.daoHelper = daoHelper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public GiftCertificate save(GiftCertificate certificate, Set<Tag> tags) {
        saveTags(tags);
        GiftCertificate savedCertificate = saveCertificate(certificate);
        saveReferencesBetweenCertificatesAndTags(savedCertificate.getId(), tags);
        return savedCertificate;
    }

    private GiftCertificate saveCertificate(GiftCertificate certificate) {
        return getById(daoHelper.updateTableWithIdReturn(SAVE_CERTIFICATE, certificate));
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
        return daoHelper.getAllEntityFromTable(GET_ALL_CERTIFICATES, mapper);
    }

    @Override
    public GiftCertificate getById(long id) {
        return daoHelper.getEntityFromTable(GET_CERTIFICATE_BY_ID, id, mapper);
    }

    @Override
    public GiftCertificate getByName(String name) {
        return daoHelper.getEntityFromTable(GET_CERTIFICATE_BY_NAME, name, mapper);
    }

    @Override
    public List<GiftCertificate> getByTagName(List<String> sortTypes, String name) {
        return daoHelper.getEntityListFromTable(substituteSqlQueryVariable(defineSortType(sortTypes),
                GET_CERTIFICATES_BY_TAG_NAME),
                getParameterMap(name, null), mapper);
    }

    private String defineSortType(List<String> sortTypes) {
        return isSortTypeExists(sortTypes) ? getTableFieldsForSorting(sortTypes) : "";
    }

    private boolean isSortTypeExists(List<String> sortTypes) {
        return sortTypes != null && sortTypes.size() > 0;
    }

    private String getTableFieldsForSorting(List<String> sortTypes) {
        sortTypes = getSortingParams(sortTypes);
        return isSortTypeExists(sortTypes) ? produceParams(sortTypes) : "";
    }

    private List<String> getSortingParams(List<String> sortTypes) {
        return sortTypes.stream()
                .filter(SORTABLE_TABLE_FIELDS::contains)
                .peek(type -> type = type + SORTING_ORDER)
                .collect(Collectors.toList());
    }

    private String produceParams(List<String> params) {
        return "ORDER BY " + String.join(", ", params);
    }

    private String substituteSqlQueryVariable(String value, String source) {
        return new StringSubstitutor(Collections.singletonMap("value", value)).replace(source);
    }

    @Override
    public List<GiftCertificate> searchByPartNameOrDescription(List<String> sortTypes,
                                                               String partNameOrDescription) {
        return daoHelper.getEntityListFromTable(substituteSqlQueryVariable(defineSortType(sortTypes),
                GET_CERTIFICATES_BY_PART_NAME_OR_DESCRIPTION),
                getParameterMap(prepareParameterForInsertingToSqlScript(partNameOrDescription), null),
                mapper);
    }

    @Override
    public List<GiftCertificate> searchByTagAndPartNameOrDescription(List<String> sortTypes, String tagName,
                                                                     String text) {
        return daoHelper.getEntityListFromTable(
                substituteSqlQueryVariable(defineSortType(sortTypes),
                        GET_CERTIFICATE_BY_TAG_NAME_AND_PART_OF_NAME_OR_DESCRIPTION),
                getParameterMap(tagName, prepareParameterForInsertingToSqlScript(text)), mapper);
    }

    private Map<String, String> getParameterMap(String param, @Nullable String text) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("param", param);
        parameters.put("text", text);
        return parameters;
    }

    private String prepareParameterForInsertingToSqlScript(String partNameOrDescription) {
        return "%" + partNameOrDescription + "%";
    }

    @Override
    @Transactional
    public GiftCertificate update(Map<String, Object> updatableInfo, Set<Tag> tags) {
        long id = (long) updatableInfo.get("id");
        String fields = getUpdatableParameters(updatableInfo.keySet());
        daoHelper.updateTable(substituteSqlQueryVariable(fields, UPDATE_CERTIFICATE), updatableInfo);
        updateReferencesBetweenCertificatesAndTagsIfTagsWasPassForIt(id, tags);
        return getById(id);
    }

    private String getUpdatableParameters(Set<String> fields) {
        StringBuilder sb = new StringBuilder();
        fields.stream()
                .filter(field -> !field.equals("id"))
                .forEach(field -> sb.append(field).append("=").append(":").append(field).append(" "));
        return formatUpdatableParameters(sb);
    }

    private String formatUpdatableParameters(StringBuilder stringBuilder) {
        return String.join(", ", stringBuilder.toString().split(" "));
    }

    private void updateReferencesBetweenCertificatesAndTagsIfTagsWasPassForIt(long id, Set<Tag> tags) {
        if (isTagsPassedForUpdate(tags)) {
            updateReferencesBetweenCertificatesAndTags(id, tags);
        }
    }

    private boolean isTagsPassedForUpdate(Set<Tag> tags) {
        return tags != null && tags.size() > 0;
    }

    private void updateReferencesBetweenCertificatesAndTags(long id, Set<Tag> tags) {
        saveTags(tags);
        daoHelper.updateTable(DELETE_REFERENCES_BETWEEN_CERTIFICATES_AND_TAGS, Collections.singletonMap("id", id));
        saveReferencesBetweenCertificatesAndTags(id, tags);
    }

    private void saveTags(Set<Tag> tags) {
        List<Tag> registerTags = tagDao.getAll();
        getRegisterTagIds(tags, registerTags);
        saveUnregisterTags(tags, registerTags);
    }

    private void getRegisterTagIds(Set<Tag> tags, List<Tag> registerTags) {
        tags.stream()
                .filter(registerTags::contains)
                .forEach(tag -> tag.setId(registerTags.get(registerTags.indexOf(tag)).getId()));
    }

    private void saveUnregisterTags(Set<Tag> tags, List<Tag> registerTags) {
        tags.stream()
                .filter(tag -> !registerTags.contains(tag))
                .forEach(tag -> tag.setId(tagDao.save(tag).getId()));
    }

    @Override
    public void delete(GiftCertificate certificate) {
        daoHelper.updateTable(DELETE_CERTIFICATE, certificate);
    }
}
