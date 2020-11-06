package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.lang.Nullable;

import java.util.List;

public interface GiftCertificateDao {
    void save(GiftCertificate certificate, List<Tag> tags);

    List<GiftCertificate> getAll();

    GiftCertificate getById(long id);

    GiftCertificate get(String certificateName);

    List<GiftCertificate> getByTagName(String tagName);

    List<GiftCertificate> searchByPartNameOrDescription(String partName);

    void update(GiftCertificate certificate, String[] fields, @Nullable List<Tag> tags);

    void delete(GiftCertificate certificate);
}
