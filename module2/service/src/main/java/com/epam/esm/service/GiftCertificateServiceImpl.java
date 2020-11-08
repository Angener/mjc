package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificatesWithTagsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.UpdatingForbiddenFieldsException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GiftCertificateServiceImpl implements GiftCertificateService {
    GiftCertificateDao giftCertificateDao;
    TagService tagService;

    Comparator<GiftCertificatesWithTagsDto> dateComparator = Comparator.comparing(d ->
            d.getCertificate().getCreateDate());
    Comparator<GiftCertificatesWithTagsDto> nameComparator = Comparator.comparing(d -> d.getCertificate().getName());

    @Override
    @Transactional
    public long save(GiftCertificateDto dto) {
        GiftCertificate certificate = dto.getGiftCertificate();
        Set<Tag> tags = dto.getTags();
        return giftCertificateDao.save(certificate, tags);
    }

    @Override
    @Transactional
    public void update(GiftCertificateDto dto) throws UpdatingForbiddenFieldsException {
        checkUpdatableFields(dto.getFields());
        updateGiftCertificate(dto.getGiftCertificate(), dto.getFields(), dto.getTags());
    }

    private void checkUpdatableFields(String[] fields) throws UpdatingForbiddenFieldsException {
        if (isUpdatableRequestContainsForbiddenFields(fields)) {
            throw new UpdatingForbiddenFieldsException();
        }
    }

    private boolean isUpdatableRequestContainsForbiddenFields(String[] fields) {
        return Arrays.stream(fields)
                .anyMatch(field -> field.trim().toLowerCase().equals("createdate") ||
                        field.trim().toLowerCase().equals("lastupdatedate"));
    }

    private void updateGiftCertificate(GiftCertificate certificate, String[] fields, @Nullable Set<Tag> tags) {
        giftCertificateDao.update(certificate, fields, tags);
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
    public List<GiftCertificatesWithTagsDto> get(@Nullable String tagName, @Nullable String partOfNameOrDesc,
                                                 boolean nameSort, boolean dateSort) {
        List<GiftCertificatesWithTagsDto> certificates = new ArrayList<>(
                collectCertificatesWithTags(tagName, partOfNameOrDesc));
        collectAllCertificatesTags(certificates);
        sortCertificates(certificates, nameSort, dateSort);
        return certificates;
    }

    private Set<GiftCertificatesWithTagsDto> collectCertificatesWithTags(String tagName, String partOfName) {
        return (isSearchParametersPassed(tagName, partOfName)) ? collect(tagName, partOfName) : new HashSet<>();
    }

    private boolean isSearchParametersPassed(String tagName, String partOfName) {
        return tagName.trim().length() > 0 || partOfName.trim().length() > 0;
    }

    private Set<GiftCertificatesWithTagsDto> collect(String tagName, String partOfName) {
        return (tagName.trim().length() > 0) ?
                collectCertificatesByTagNameWithOrWithoutByPartNameOrDescription(tagName, partOfName)
                : (collectBePartNameOrDescriptionOnly(partOfName));
    }

    private Set<GiftCertificatesWithTagsDto> collectCertificatesByTagNameWithOrWithoutByPartNameOrDescription
            (String tagName, String partOfName) {
        return ((partOfName.trim().length() > 0) ? (collectByBothParameters(tagName, partOfName)) :
                (collectByTagNameOnly(tagName)));
    }

    private Set<GiftCertificatesWithTagsDto> collectByBothParameters(String tagName, String partNameOrDescription) {
        Set<GiftCertificatesWithTagsDto> certificates = collectCertificates(getByTagName(tagName));
        certificates.addAll(collectCertificates(searchByPartNameOrDescription(partNameOrDescription)));
        return certificates;
    }

    private Set<GiftCertificatesWithTagsDto> collectCertificates(List<GiftCertificate> certificates) {
        return certificates.stream()
                .map(GiftCertificatesWithTagsDto::new)
                .collect(Collectors.toSet());
    }

    private Set<GiftCertificatesWithTagsDto> collectByTagNameOnly(String tagName) {
        return collectCertificates(getByTagName(tagName));
    }

    private Set<GiftCertificatesWithTagsDto> collectBePartNameOrDescriptionOnly(String partNameOrDescription) {
        return collectCertificates(searchByPartNameOrDescription(partNameOrDescription));
    }

    private void collectAllCertificatesTags(List<GiftCertificatesWithTagsDto> certificates) {
        certificates.forEach(c -> c.setTags(tagService.getAllGiftCertificateTags(c.getCertificate())));
    }

    private void sortCertificates(List<GiftCertificatesWithTagsDto> certificates, boolean nameSort, boolean dateSort) {
        if (isSortingRequired(nameSort, dateSort)) {
            defineSortType(certificates, nameSort, dateSort);
        }
    }

    private boolean isSortingRequired(boolean nameSort, boolean dateSort) {
        return nameSort || dateSort;
    }

    private void defineSortType(List<GiftCertificatesWithTagsDto> certificates, boolean nameSort, boolean dateSort) {
        if (nameSort && dateSort) {
            sortByDateAndName(certificates);
        } else {
            sortByDateOrByName(certificates, nameSort);
        }
    }

    private void sortByDateAndName(List<GiftCertificatesWithTagsDto> certificates) {
        certificates.sort(dateComparator.thenComparing(nameComparator));
    }

    private void sortByDateOrByName(List<GiftCertificatesWithTagsDto> certificates, boolean nameSort) {
        if (nameSort) {
            certificates.sort(nameComparator);
        } else {
            certificates.sort(dateComparator);
        }
    }

    @Override
    public GiftCertificate get(String certificateName) {
        return giftCertificateDao.getByName(certificateName);
    }

    @Override
    public List<GiftCertificate> getByTagName(String tagName) {
        return giftCertificateDao.getByTagName(tagName);
    }

    @Override
    public List<GiftCertificate> searchByPartNameOrDescription(String partName) {
        return giftCertificateDao.searchByPartNameOrDescription(partName);
    }

    @Override
    public void delete(GiftCertificate certificate) {
        giftCertificateDao.delete(certificate);
    }
}
