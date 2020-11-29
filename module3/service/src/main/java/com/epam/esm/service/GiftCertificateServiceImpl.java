package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.entity.GiftCertificate;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateDao giftCertificateDao;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateDao giftCertificateDao) {
        this.giftCertificateDao = giftCertificateDao;
    }

    @Override
    @Transactional
    public GiftCertificate save(GiftCertificate certificate) {
        return giftCertificateDao.save(certificate);
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificate certificate) {
        return giftCertificateDao.update(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCertificate> getAll() {
        return giftCertificateDao.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCertificate> getAll(int page, int recordsPerPage) {
        return recordsPerPage > 0 ? getPaginateCertificates(page, recordsPerPage) : getAll();
    }

    private List<GiftCertificate> getPaginateCertificates(int page, int recordsPerPage) {
        int startPosition = getStartPosition(page, recordsPerPage);
        int recordsQuantity = getRecordsQuantity(startPosition, recordsPerPage,
                (int) giftCertificateDao.getGiftCertificatesQuantity());
        return checkResultCertificatesList(giftCertificateDao.getAll(startPosition, recordsQuantity));
    }

    private List<GiftCertificate> checkResultCertificatesList(List<GiftCertificate> certificates) {
        Preconditions.checkArgument(certificates.size() > 0);
        return certificates;
    }

    private int getStartPosition(int page, int recordsPerPage) {
        return (page == 0) ? (0) : (page * recordsPerPage);
    }

    private int getRecordsQuantity(int startPosition, int recordsPerPage, int certificatesQuantity) {
        int menuSize = certificatesQuantity - startPosition;
        int recordsQuantity = Math.min(recordsPerPage, menuSize);
        Preconditions.checkArgument(recordsQuantity > 0);
        return recordsQuantity;
    }

    @Override
    @Transactional(readOnly = true)
    public GiftCertificate getById(int id) {
        return giftCertificateDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCertificate> search(@Nullable Set<String> tagNames, @Nullable String partOfNameOrDesc,
                                        @Nullable List<String> sortTypes) {
        return (isSearchParametersPassed(tagNames, partOfNameOrDesc)) ?
                collect(sortTypes, tagNames, partOfNameOrDesc) : getAll();
    }

    private boolean isSearchParametersPassed(Set<String> tagNames, String partOfName) {
        return isTagNamesPassed(tagNames) || isPartNameOrDescriptionPassed(partOfName);
    }

    private boolean isPartNameOrDescriptionPassed(String param) {
        return param != null && param.trim().length() > 0;
    }

    private boolean isTagNamesPassed(Set<String> tagNames) {
        return tagNames != null && tagNames.size() > 0;
    }

    private List<GiftCertificate> collect(List<String> sortTypes, Set<String> tagNames, String partOfName) {
        return (isTagNamesPassed(tagNames)) ?
                collectByTagAndPartNameOrDescription(sortTypes, tagNames, partOfName)
                : (collectByPartNameOrDescriptionOnly(sortTypes, partOfName));
    }

    private List<GiftCertificate> collectByTagAndPartNameOrDescription
            (List<String> sortTypes, Set<String> tagNames, String partOfName) {
        return ((isPartNameOrDescriptionPassed(partOfName)) ?
                (collectByBothParameters(sortTypes, tagNames, partOfName)) :
                (collectByTagNameOnly(sortTypes, tagNames)));
    }

    private List<GiftCertificate> collectByBothParameters(List<String> sortTypes,
                                                          Set<String> tagNames,
                                                          String partNameOrDescription) {
        return giftCertificateDao.searchByTagAndPartNameOrDescription(sortTypes, tagNames, partNameOrDescription);
    }

    private List<GiftCertificate> collectByTagNameOnly(List<String> sortTypes, Set<String> tagNames) {
        return giftCertificateDao.getByTagName(sortTypes, tagNames);
    }

    private List<GiftCertificate> collectByPartNameOrDescriptionOnly(List<String> sortTypes,
                                                                     String partNameOrDescription) {
        return giftCertificateDao.searchByPartNameOrDescription(sortTypes, partNameOrDescription);
    }

    @Override
    public List<GiftCertificate> getPaginatedCertificateList(List<GiftCertificate> certificates,
                                                             int page, int recordsPerPage) {
        int startPosition = getStartPosition(page, recordsPerPage);
        int endPosition = startPosition + getRecordsQuantity(startPosition, recordsPerPage, certificates.size());
        return checkResultCertificatesList(certificates.subList(startPosition, endPosition));
    }

    @Override
    @Transactional(readOnly = true)
    public GiftCertificate search(String certificateName) {
        return giftCertificateDao.getByName(certificateName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCertificate> getByTagName(Set<String> tagNames) {
        return giftCertificateDao.getByTagName(null, tagNames);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCertificate> searchByPartNameOrDescription(String partName) {
        return giftCertificateDao.searchByPartNameOrDescription(null, partName);
    }

    @Override
    @Transactional
    public void delete(GiftCertificate certificate) {
        giftCertificateDao.delete(certificate);
    }
}
