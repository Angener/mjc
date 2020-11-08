package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

public interface GiftCertificateDao {
    long save(GiftCertificate certificate, Set<Tag> tags);

    List<GiftCertificate> getAll();

    GiftCertificate getById(long id);

    GiftCertificate getByName(String certificateName);

    List<GiftCertificate> getByTagName(String tagName);

    List<GiftCertificate> searchByPartNameOrDescription(String partName);

    void update(GiftCertificate certificate, String[] fields, @Nullable Set<Tag> tags);

    void delete(GiftCertificate certificate);
}
