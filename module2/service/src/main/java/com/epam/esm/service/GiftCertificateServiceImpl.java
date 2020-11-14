package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateWithTagsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateDao giftCertificateDao;
    private final TagService tagService;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateDao giftCertificateDao, TagService tagService) {
        this.giftCertificateDao = giftCertificateDao;
        this.tagService = tagService;
    }

    @Override
    @Transactional
    public GiftCertificate save(GiftCertificateDto dto) {
        GiftCertificate certificate = dto.getGiftCertificate();
        Set<Tag> tags = dto.getTags();
        return giftCertificateDao.save(certificate, tags);
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificateDto dto) {
        Map<String, Object> map = putFieldsToMap(dto.getGiftCertificate());
        clearNullableFields(map);
        Set<Tag> tags = dto.getTags();
        return updateGiftCertificate(map, tags);
    }

    private Map<String, Object> putFieldsToMap(GiftCertificate certificate) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", certificate.getId());
        fields.put("name", certificate.getName());
        fields.put("description", certificate.getDescription());
        fields.put("price", certificate.getPrice());
        fields.put("duration", certificate.getDuration());
        return fields;
    }

    private void clearNullableFields(Map<String, Object> fields) {
        fields.values().removeIf(Objects::isNull);
        fields.values().removeIf(field -> field.equals(0));
    }

    private GiftCertificate updateGiftCertificate(Map<String, Object> fields, Set<Tag> tags) {
        return giftCertificateDao.update(fields, tags);
    }

    @Override
    public List<GiftCertificate> getAll() {
        return giftCertificateDao.getAll();
    }

    @Override
    public GiftCertificate getById(long id) {
        return giftCertificateDao.getById(id);
    }


    @Override
    public List<GiftCertificateWithTagsDto> search(@Nullable String tagName, @Nullable String partOfNameOrDesc,
                                                   @Nullable List<String> sortTypes) {
        List<GiftCertificateWithTagsDto> certificates = collectCertificates(collectCertificatesWithTags(sortTypes,
                tagName, partOfNameOrDesc));
        collectAllCertificatesTags(certificates);
        return certificates;
    }

    private List<GiftCertificate> collectCertificatesWithTags(List<String> sortTypes,
                                                              String tagName,
                                                              String partOfName) {
        return (isSearchParametersPassed(tagName, partOfName)) ?
                collect(sortTypes, tagName, partOfName) : new ArrayList<>();
    }

    private boolean isSearchParametersPassed(String tagName, String partOfName) {
        return tagName.trim().length() > 0 || partOfName.trim().length() > 0;
    }

    private List<GiftCertificate> collect(List<String> sortTypes, String tagName, String partOfName) {
        return (tagName.trim().length() > 0) ?
                collectByTagAndPartNameOrDescription(sortTypes, tagName, partOfName)
                : (collectByPartNameOrDescriptionOnly(sortTypes, partOfName));
    }

    private List<GiftCertificate> collectByTagAndPartNameOrDescription
            (List<String> sortTypes, String tagName, String partOfName) {
        return ((partOfName.trim().length() > 0) ? (collectByBothParameters(sortTypes, tagName, partOfName)) :
                (collectByTagNameOnly(sortTypes, tagName)));
    }

    protected List<GiftCertificate> collectByBothParameters(List<String> sortTypes,
                                                            String tagName,
                                                            String partNameOrDescription) {
        return giftCertificateDao.searchByTagAndPartNameOrDescription(sortTypes, tagName, partNameOrDescription);
    }

    protected List<GiftCertificateWithTagsDto> collectCertificates(List<GiftCertificate> certificates) {
        return certificates.stream()
                .map(GiftCertificateWithTagsDto::new)
                .collect(Collectors.toList());
    }

    protected List<GiftCertificate> collectByTagNameOnly(List<String> sortTypes, String tagName) {
        return giftCertificateDao.getByTagName(sortTypes, tagName);
    }

    protected List<GiftCertificate> collectByPartNameOrDescriptionOnly(List<String> sortTypes,
                                                                       String partNameOrDescription) {
        return giftCertificateDao.searchByPartNameOrDescription(sortTypes, partNameOrDescription);
    }

    protected void collectAllCertificatesTags(List<GiftCertificateWithTagsDto> certificates) {
        certificates.forEach(c -> c.setTags(tagService.getAllGiftCertificateTags(c.getCertificate())));
    }

    @Override
    public GiftCertificate search(String certificateName) {
        return giftCertificateDao.getByName(certificateName);
    }

    @Override
    public List<GiftCertificate> getByTagName(String tagName) {
        return giftCertificateDao.getByTagName(null, tagName);
    }

    @Override
    public List<GiftCertificate> searchByPartNameOrDescription(String partName) {
        return giftCertificateDao.searchByPartNameOrDescription(null, partName);
    }

    @Override
    public void delete(GiftCertificate certificate) {
        giftCertificateDao.delete(certificate);
    }
}
