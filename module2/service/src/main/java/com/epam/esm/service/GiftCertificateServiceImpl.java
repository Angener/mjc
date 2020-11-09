package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateWithTagsDto;
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

    Comparator<GiftCertificateWithTagsDto> dateComparator = Comparator.comparing(d ->
            d.getCertificate().getCreateDate());
    Comparator<GiftCertificateWithTagsDto> nameComparator = Comparator.comparing(d -> d.getCertificate().getName());

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
    public List<GiftCertificateWithTagsDto> search(@Nullable String tagName, @Nullable String partOfNameOrDesc,
                                                   boolean nameSort, boolean dateSort) {
        List<GiftCertificateWithTagsDto> certificates = convertToList(tagName, partOfNameOrDesc);
        collectAllCertificatesTags(certificates);
        sortCertificates(certificates, nameSort, dateSort);
        return certificates;
    }

    private List<GiftCertificateWithTagsDto> convertToList(String tagName, String partOfName){
        return new ArrayList<>(collectCertificatesWithTags(tagName, partOfName));
    }

    private Set<GiftCertificateWithTagsDto> collectCertificatesWithTags(String tagName, String partOfName) {
        return (isSearchParametersPassed(tagName, partOfName)) ? collect(tagName, partOfName) : new HashSet<>();
    }

    private boolean isSearchParametersPassed(String tagName, String partOfName) {
        return tagName.trim().length() > 0 || partOfName.trim().length() > 0;
    }

    private Set<GiftCertificateWithTagsDto> collect(String tagName, String partOfName) {
        return (tagName.trim().length() > 0) ?
                collectCertificatesByTagNameWithOrWithoutByPartNameOrDescription(tagName, partOfName)
                : (collectByPartNameOrDescriptionOnly(partOfName));
    }

    private Set<GiftCertificateWithTagsDto> collectCertificatesByTagNameWithOrWithoutByPartNameOrDescription
            (String tagName, String partOfName) {
        return ((partOfName.trim().length() > 0) ? (collectByBothParameters(tagName, partOfName)) :
                (collectByTagNameOnly(tagName)));
    }

    protected Set<GiftCertificateWithTagsDto> collectByBothParameters(String tagName, String partNameOrDescription) {
        Set<GiftCertificateWithTagsDto> certificates = collectCertificates(getByTagName(tagName));
        certificates.addAll(collectCertificates(searchByPartNameOrDescription(partNameOrDescription)));
        return certificates;
    }

    protected Set<GiftCertificateWithTagsDto> collectCertificates(List<GiftCertificate> certificates) {
        return certificates.stream()
                .map(GiftCertificateWithTagsDto::new)
                .collect(Collectors.toSet());
    }

    protected Set<GiftCertificateWithTagsDto> collectByTagNameOnly(String tagName) {
        return collectCertificates(getByTagName(tagName));
    }

    protected Set<GiftCertificateWithTagsDto> collectByPartNameOrDescriptionOnly(String partNameOrDescription) {
        return collectCertificates(searchByPartNameOrDescription(partNameOrDescription));
    }

    protected void collectAllCertificatesTags(List<GiftCertificateWithTagsDto> certificates) {
        certificates.forEach(c -> c.setTags(tagService.getAllGiftCertificateTags(c.getCertificate())));
    }

    private void sortCertificates(List<GiftCertificateWithTagsDto> certificates, boolean nameSort, boolean dateSort) {
        if (isSortingRequired(nameSort, dateSort)) {
            defineSortType(certificates, nameSort, dateSort);
        }
    }

    private boolean isSortingRequired(boolean nameSort, boolean dateSort) {
        return nameSort || dateSort;
    }

    private void defineSortType(List<GiftCertificateWithTagsDto> certificates, boolean nameSort, boolean dateSort) {
        if (nameSort && dateSort) {
            sortByDateAndName(certificates);
        } else {
            sortByDateOrByName(certificates, nameSort);
        }
    }

    protected void sortByDateAndName(List<GiftCertificateWithTagsDto> certificates) {
        certificates.sort(dateComparator.thenComparing(nameComparator));
    }

    protected void sortByDateOrByName(List<GiftCertificateWithTagsDto> certificates, boolean nameSort) {
        if (nameSort) {
            certificates.sort(nameComparator);
        } else {
            certificates.sort(dateComparator);
        }
    }

    @Override
    public GiftCertificate search(String certificateName) {
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
