package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.lang.Nullable;

import java.util.List;

public interface GiftCertificateService {
    GiftCertificate save(GiftCertificate certificate);

    List<GiftCertificate> getAll();

    List<GiftCertificate> search(@Nullable String tagName, @Nullable String partOfNameOrDesc,
                                 @Nullable List<String> sortTypes);

    GiftCertificate getById(long id);

    GiftCertificate search(String certificateName);

    List<GiftCertificate> getByTagName(String tagName);

    List<GiftCertificate> searchByPartNameOrDescription(String partName);

    GiftCertificate update(GiftCertificate certificate);

    void delete(GiftCertificate certificate);
}
