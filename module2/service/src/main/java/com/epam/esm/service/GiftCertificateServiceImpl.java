package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.SortCertificatesType;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateWithTagsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GiftCertificateServiceImpl implements GiftCertificateService {
    GiftCertificateDao giftCertificateDao;
    TagService tagService;

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
                                                   boolean nameSort, boolean dateSort) {
        SortCertificatesType type = getSortType(nameSort, dateSort);
        List<GiftCertificateWithTagsDto> certificates = collectCertificates(collectCertificatesWithTags(type,
                tagName, partOfNameOrDesc));
        collectAllCertificatesTags(certificates);
        return certificates;
    }

    protected SortCertificatesType getSortType(boolean nameSort, boolean dateSort) {
        return isSortingRequired(nameSort, dateSort) ? defineSortType(nameSort, dateSort) : SortCertificatesType.NONE;
    }

    private boolean isSortingRequired(boolean nameSort, boolean dateSort) {
        return nameSort || dateSort;
    }

    protected SortCertificatesType defineSortType(boolean nameSort, boolean dateSort) {
        if (nameSort && dateSort) {
            return SortCertificatesType.DATE_AND_NAME_SORT;
        } else {
            return nameSort ? SortCertificatesType.NAME_SORT : SortCertificatesType.DATE_SORT;
        }
    }

    private List<GiftCertificate> collectCertificatesWithTags(SortCertificatesType type, String tagName, String partOfName) {
        return (isSearchParametersPassed(tagName, partOfName)) ? collect(type, tagName, partOfName) : new ArrayList<>();
    }

    private boolean isSearchParametersPassed(String tagName, String partOfName) {
        return tagName.trim().length() > 0 || partOfName.trim().length() > 0;
    }

    private List<GiftCertificate> collect(SortCertificatesType type, String tagName, String partOfName) {
        return (tagName.trim().length() > 0) ?
                collectByTagAndPartNameOrDescription(type, tagName, partOfName)
                : (collectByPartNameOrDescriptionOnly(type, partOfName));
    }

    private List<GiftCertificate> collectByTagAndPartNameOrDescription
            (SortCertificatesType type, String tagName, String partOfName) {
        return ((partOfName.trim().length() > 0) ? (collectByBothParameters(type, tagName, partOfName)) :
                (collectByTagNameOnly(type, tagName)));
    }

    protected List<GiftCertificate> collectByBothParameters(SortCertificatesType type, String tagName, String partNameOrDescription) {
        return giftCertificateDao.searchByTagAndPartNameOrDescription(type, tagName, partNameOrDescription);
    }

    protected List<GiftCertificateWithTagsDto> collectCertificates(List<GiftCertificate> certificates) {
        return certificates.stream()
                .map(GiftCertificateWithTagsDto::new)
                .collect(Collectors.toList());
    }

    protected List<GiftCertificate> collectByTagNameOnly(SortCertificatesType type, String tagName) {
        return giftCertificateDao.getByTagName(type, tagName);
    }

    protected List<GiftCertificate> collectByPartNameOrDescriptionOnly(SortCertificatesType type, String partNameOrDescription) {
        return giftCertificateDao.searchByPartNameOrDescription(type, partNameOrDescription);
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
        return giftCertificateDao.getByTagName(SortCertificatesType.NONE, tagName);
    }

    @Override
    public List<GiftCertificate> searchByPartNameOrDescription(String partName) {
        return giftCertificateDao.searchByPartNameOrDescription(SortCertificatesType.NONE, partName);
    }

    @Override
    public void delete(GiftCertificate certificate) {
        giftCertificateDao.delete(certificate);
    }
}
