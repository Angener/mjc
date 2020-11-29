package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.entity.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public List<GiftCertificate> getAll() {
        return giftCertificateDao.getAll();
    }

    @Override
    public GiftCertificate getById(long id) {
        return giftCertificateDao.getById(id);
    }

    @Override
    public List<GiftCertificate> search(@Nullable Set<String> tagNames, @Nullable String partOfNameOrDesc,
                                        @Nullable List<String> sortTypes) {
        return (isSearchParametersPassed(tagNames, partOfNameOrDesc)) ?
                collect(sortTypes, tagNames, partOfNameOrDesc) : new ArrayList<>();
    }

    private boolean isSearchParametersPassed(Set<String> tagNames, String partOfName) {
        return isTagNamesPassed(tagNames) || isPartNameOrDescriptionPassed(partOfName);
    }

    private boolean isPartNameOrDescriptionPassed(String param) {
        return param != null && param.trim().length() > 0;
    }

    private boolean isTagNamesPassed(Set<String> tagNames){
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
    public GiftCertificate search(String certificateName) {
        return giftCertificateDao.getByName(certificateName);
    }

    @Override
    public List<GiftCertificate> getByTagName(Set<String> tagNames) {
        return giftCertificateDao.getByTagName(null, tagNames);
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
