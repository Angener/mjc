package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.entity.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public List<GiftCertificate> search(@Nullable String tagName, @Nullable String partOfNameOrDesc,
                                        @Nullable List<String> sortTypes) {
        return (isSearchParametersPassed(tagName, partOfNameOrDesc)) ?
                collect(sortTypes, tagName, partOfNameOrDesc) : new ArrayList<>();
    }

    private boolean isSearchParametersPassed(String tagName, String partOfName) {
        return isParameterPassed(tagName) || isParameterPassed(partOfName);
    }

    private boolean isParameterPassed(String param) {
        return param != null && param.trim().length() > 0;
    }

    private List<GiftCertificate> collect(List<String> sortTypes, String tagName, String partOfName) {
        return (isParameterPassed(tagName)) ?
                collectByTagAndPartNameOrDescription(sortTypes, tagName, partOfName)
                : (collectByPartNameOrDescriptionOnly(sortTypes, partOfName));
    }

    private List<GiftCertificate> collectByTagAndPartNameOrDescription
            (List<String> sortTypes, String tagName, String partOfName) {
        return ((isParameterPassed(partOfName)) ? (collectByBothParameters(sortTypes, tagName, partOfName)) :
                (collectByTagNameOnly(sortTypes, tagName)));
    }

    private List<GiftCertificate> collectByBothParameters(List<String> sortTypes,
                                                          String tagName,
                                                          String partNameOrDescription) {
        return giftCertificateDao.searchByTagAndPartNameOrDescription(sortTypes, tagName, partNameOrDescription);
    }

    private List<GiftCertificate> collectByTagNameOnly(List<String> sortTypes, String tagName) {
        return giftCertificateDao.getByTagName(sortTypes, tagName);
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
