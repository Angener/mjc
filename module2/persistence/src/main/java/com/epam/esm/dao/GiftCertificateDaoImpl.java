package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiftCertificateDaoImpl extends Dao<GiftCertificate> implements GiftCertificateDao {

    TagDao tagDao;

    @Autowired
    public GiftCertificateDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                  SimpleJdbcInsert simpleJdbcInsert,
                                  JdbcTemplate jdbcTemplate,
                                  TagDao tagDao) {
        super(jdbcTemplate, namedParameterJdbcTemplate, simpleJdbcInsert);
        this.tagDao = tagDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public long save(GiftCertificate certificate, List<Tag> tags) {
        long id;
        saveTags(tags);
        id = updateTableWithIdReturn(SqlScript.SAVE_CERTIFICATE.getScript(), certificate);
        saveReferencesBetweenCertificatesAndTags(getByName(certificate.getName()), updateTagsId(tags));
        return id;
    }

    private void saveTags(List<Tag> tags) {
        tags.forEach(tagDao::save);
    }

    private void saveReferencesBetweenCertificatesAndTags(GiftCertificate certificate, List<Tag> tags) {
        tags.forEach(
                tag -> {
                    Map<String, Object> parameter = new HashMap<>();
                    parameter.put("tag_id", tag.getId());
                    parameter.put("gift_certificate_id", certificate.getId());
                    simpleJdbcInsert.execute(parameter);
                });
    }

    private List<Tag> updateTagsId(List<Tag> tags) {
        return tags.stream()
                .map(tag -> tagDao.getByName(tag.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GiftCertificate> getAll() {
        return getAllEntityFromTable(SqlScript.GET_ALL_CERTIFICATES.getScript(), getGiftCertificateRowMap());
    }

    @Override
    public GiftCertificate getById(long id) {
        return getEntityFromTable(SqlScript.GET_CERTIFICATE_BY_ID.getScript(), id, getGiftCertificateRowMap());
    }

    @Override
    public GiftCertificate getByName(String name) {
        return getEntityFromTable(SqlScript.GET_CERTIFICATE_BY_NAME.getScript(), name, getGiftCertificateRowMap());
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
        return getEntityListFromTable(SqlScript.GET_CERTIFICATES_BY_TAG_NAME.getScript(), name, getGiftCertificateRowMap());
    }

    @Override
    public List<GiftCertificate> searchByPartNameOrDescription(String partNameOrDescription) {
        return getEntityListFromTable(
                SqlScript.GET_CERTIFICATES_BY_PART_NAME_OR_DESCRIPTION.getScript(),
                prepareParameterForInsertingToSqlScript(partNameOrDescription),
                getGiftCertificateRowMap());
    }

    private String prepareParameterForInsertingToSqlScript(String partNameOrDescription) {
        return "%" + partNameOrDescription + "%";
    }

    @Override
    @Transactional
    public void update(GiftCertificate certificate, String[] fields, @Nullable List<Tag> tags) {
        updateTable(getUpdatingSqlScript(fields), certificate);
        updateReferencesBetweenCertificatesAndTagsIfTagsWasPassForIt(certificate, tags);
    }

    private String getUpdatingSqlScript(String[] fields) {
        return SqlScript.UPDATE_CERTIFICATE.getScript()
                .replace("?INSERT FIELDS?", getUpdatableParameters(fields));
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
        updateTable(SqlScript.DELETE_REFERENCES_BETWEEN_CERTIFICATES_AND_TAGS.getScript(), certificate);
        saveReferencesBetweenCertificatesAndTags(certificate, updateTagsId(tags));
    }

    @Override
    public void delete(GiftCertificate certificate) {
        updateTable(SqlScript.DELETE_CERTIFICATE.getScript(), certificate);
    }
}
