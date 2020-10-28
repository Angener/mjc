package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService{
    @Autowired
    GiftCertificateDao giftCertificateDao;
    @Override
    public void save(GiftCertificate certificate, List<Tag> tags) throws DataIntegrityViolationException {
        giftCertificateDao.save(certificate, tags);
    }

    @Override
    public GiftCertificate get(String certificateName) {
        return giftCertificateDao.get(certificateName);
    }

    @Override
    public List<GiftCertificate> getByTagName(String tagName) {
        return giftCertificateDao.getByTagName(tagName);
    }

    @Override
    public List<GiftCertificate> getByPartName(String partName) {
        return giftCertificateDao.getByPartName(partName);
    }

    @Override
    public void update(GiftCertificate certificate, String[] fields, String updatableName) {
        giftCertificateDao.update(certificate, fields, updatableName);
    }

    @Override
    public void delete(GiftCertificate certificate) {
        giftCertificateDao.delete(certificate);
    }
}
