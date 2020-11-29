package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

public interface GiftCertificateService {
    GiftCertificate save(GiftCertificate certificate);

    List<GiftCertificate> getAll();

    List<GiftCertificate> search(@Nullable Set<String> tagNames, @Nullable String partOfNameOrDesc,
                                 @Nullable List<String> sortTypes);

    GiftCertificate getById(long id);

    GiftCertificate search(String certificateName);

    List<GiftCertificate> getByTagName(Set<String> tagNames);

    List<GiftCertificate> searchByPartNameOrDescription(String partName);

    GiftCertificate update(GiftCertificate certificate);

    void delete(GiftCertificate certificate);
}
