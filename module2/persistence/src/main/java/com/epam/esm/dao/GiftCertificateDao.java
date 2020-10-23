package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public interface GiftCertificateDao {
    void save(GiftCertificate certificate, List<Tag> tags) throws DataIntegrityViolationException;

    GiftCertificate get(String certificateName);

    List<GiftCertificate> getByTagName(String tagName);

    List<GiftCertificate> getByPartName(String partName);

    void update(GiftCertificate certificate, String[] fields, String updatableName);

    void delete(GiftCertificate certificate);
}
