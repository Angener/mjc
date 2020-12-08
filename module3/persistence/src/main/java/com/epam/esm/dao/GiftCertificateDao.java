package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

public interface GiftCertificateDao {

    GiftCertificate save(GiftCertificate certificate);

    List<GiftCertificate> getAll();

    List<GiftCertificate> getAll(int startPosition, int recordsQuantity);

    GiftCertificate getById(int id);

    GiftCertificate getByName(String certificateName);

    List<GiftCertificate> getByTagName(@Nullable List<String> sortTypes, Set<String> tagNames);

    List<GiftCertificate> searchByPartNameOrDescription(@Nullable List<String> sortTypes, String partName);

    List<GiftCertificate> searchByTagAndPartNameOrDescription(@Nullable List<String> sortTypes,
                                                              Set<String> tagNames, String text);

    GiftCertificate update(GiftCertificate certificate);

    void delete(GiftCertificate certificate);

    long getGiftCertificatesQuantity();
}
