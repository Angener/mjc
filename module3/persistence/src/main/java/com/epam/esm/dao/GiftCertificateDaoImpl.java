package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftCertificateDaoImpl implements GiftCertificateDao {
    static final String SORTING_ORDER = " ASC";
    static final String CONDITIONS_TYPE = " AND ";
    static final String SEARCHABLE_COLUMN = "name";
    static List<String> SORTABLE_TABLE_FIELDS = Arrays.asList("name", "createDate");

    final TagDao tagDao;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public GiftCertificateDaoImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public GiftCertificate save(GiftCertificate certificate) {
        saveTags(certificate.getTags());
        entityManager.persist(certificate);
        return certificate;
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
    @SuppressWarnings("unchecked")
    public List<GiftCertificate> getAll() {
        return (List<GiftCertificate>) entityManager.createQuery("FROM GiftCertificate").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GiftCertificate> getAll(int startPosition, int recordsQuantity) {
        return (List<GiftCertificate>) entityManager.createQuery("FROM GiftCertificate")
                .setFirstResult(startPosition)
                .setMaxResults(recordsQuantity)
                .getResultList();
    }

    @Override
    public GiftCertificate getById(int id) {
        GiftCertificate certificate = entityManager.find(GiftCertificate.class, id);
        entityManager.detach(certificate);
        return certificate;
    }

    @Override
    public GiftCertificate getByName(String name) {
        return (GiftCertificate) entityManager.createQuery("FROM GiftCertificate c WHERE c.name = :name")
                .setParameter("name", name)
                .getSingleResult();
    }

    @Override
    @SuppressWarnings("all")
    public List<GiftCertificate> getByTagName(List<String> sortTypes, Set<String> names) {
        String jpql = "SELECT c FROM GiftCertificate c JOIN c.tags t WHERE t.name ${conditions} ${value}";
        Query query = entityManager
                .createQuery(substituteJpqlQueryVariable(defineConditions(names), defineSortType(sortTypes), jpql));
        setQueryParameters(query, names);
        return query.getResultList();
    }

    private String defineConditions(Set<String> tagNames) {
        return produceConditions(prepareConditions(tagNames));
    }

    private Set<String> prepareConditions(Set<String> conditions) {
        return conditions.stream()
                .filter(condition -> condition.trim().length() > 0)
                .map(condition -> condition = ("=:").concat(condition.replaceAll(" ", "")))
                .collect(Collectors.toSet());
    }

    private String produceConditions(Set<String> conditions) {
        return String.join(CONDITIONS_TYPE.concat("t.").concat(SEARCHABLE_COLUMN), conditions);
    }

    private String substituteJpqlQueryVariable(String conditions, String sortTypes, String source) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("conditions", conditions);
        parameters.put("value", sortTypes);
        return new StringSubstitutor(parameters).replace(source);
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
                .map(type -> type = "c.".concat(type.concat(SORTING_ORDER)))
                .collect(Collectors.toList());
    }

    private String produceParams(List<String> params) {
        return "ORDER BY " + String.join(", ", params);
    }

    private void setQueryParameters(Query query, Set<String> parameters) {
        parameters.stream()
                .filter(parameter -> parameter.trim().length() > 0)
                .forEach(parameter -> {
                    String condition = parameter.replaceAll(" ", "");
                    query.setParameter(condition, parameter);
                });
    }

    private String substituteJpqlQueryVariable(String value, String source) {
        return new StringSubstitutor(Collections.singletonMap("value", value)).replace(source);
    }

    @Override
    @SuppressWarnings("all")
    public List<GiftCertificate> searchByPartNameOrDescription(List<String> sortTypes,
                                                               String partNameOrDescription) {
        String jpql = "FROM GiftCertificate c WHERE c.name LIKE :text OR c.description LIKE :text ${value}";
        return (List<GiftCertificate>) entityManager.createQuery(
                substituteJpqlQueryVariable(defineSortType(sortTypes), jpql))
                .setParameter("text", prepareParameterForInsertingToSqlScript(partNameOrDescription))
                .getResultList();
    }

    @Override
    @SuppressWarnings("all")
    public List<GiftCertificate> searchByTagAndPartNameOrDescription(List<String> sortTypes, Set<String> tagNames,
                                                                     String text) {
        String jpql = "SELECT DISTINCT c FROM GiftCertificate c JOIN c.tags t WHERE t.name ${conditions} " +
                "OR c.name LIKE :text OR c.description LIKE :text ${value}";
        Query query = entityManager
                .createQuery(substituteJpqlQueryVariable(defineConditions(tagNames), defineSortType(sortTypes), jpql))
                .setParameter("text", prepareParameterForInsertingToSqlScript(text));
        setQueryParameters(query, tagNames);
        return query.getResultList();
    }

    private String prepareParameterForInsertingToSqlScript(String partNameOrDescription) {
        return "%" + partNameOrDescription + "%";
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificate certificate) {
        GiftCertificate exists = entityManager.find(GiftCertificate.class, certificate.getId());
        saveTagsIfItWasPassed(certificate.getTags(), exists);
        updateCertificateFields(exists, getFieldsMap(certificate, exists));
        return entityManager.merge(exists);
    }

    private void saveTagsIfItWasPassed(Set<Tag> tags, GiftCertificate certificate) {
        if (tags != null) {
            saveTags(tags);
            certificate.setTags(tags);
        }
    }

    private Map<String, Object> getFieldsMap(GiftCertificate UpdatableCertificate, GiftCertificate existCertificate) {
        Map<String, Object> fields = putFieldsToMap(new HashMap<>(), UpdatableCertificate);
        putFieldsToMap(fields, existCertificate);
        return fields;
    }

    private Map<String, Object> putFieldsToMap(Map<String, Object> fields, GiftCertificate certificate) {
        fields.putIfAbsent("name", certificate.getName());
        fields.putIfAbsent("description", certificate.getDescription());
        fields.putIfAbsent("price", certificate.getPrice());
        fields.putIfAbsent("duration", certificate.getDuration());
        return fields;
    }

    private void updateCertificateFields(GiftCertificate certificate, Map<String, Object> fields) {
        certificate.setName((String) fields.get("name"));
        certificate.setDescription((String) fields.get("description"));
        certificate.setPrice((BigDecimal) fields.get("price"));
        certificate.setDuration((Integer) fields.get("duration"));
    }

    @Override
    @Transactional
    public void delete(GiftCertificate deletableCertificate) {
        entityManager.remove(entityManager.find(GiftCertificate.class, deletableCertificate.getId()));
    }

    @Override
    public long getGiftCertificatesQuantity() {
        return (long) entityManager.createQuery("SELECT COUNT(*) FROM GiftCertificate").getSingleResult();
    }
}
