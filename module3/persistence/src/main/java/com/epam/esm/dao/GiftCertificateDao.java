package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.lang.Nullable;

import java.util.List;

public interface GiftCertificateDao {

    GiftCertificate save(GiftCertificate certificate);

    List<GiftCertificate> getAll();

    GiftCertificate getById(long id);

    GiftCertificate getByName(String certificateName);

    List<GiftCertificate> getByTagName(@Nullable List<String> sortTypes, String tagName);

    List<GiftCertificate> searchByPartNameOrDescription(@Nullable List<String> sortTypes, String partName);

    List<GiftCertificate> searchByTagAndPartNameOrDescription(@Nullable List<String> sortTypes,
                                                              String tagName, String text);

    GiftCertificate update(GiftCertificate certificate);

    void delete(GiftCertificate certificate);
}
