package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.UpdatingForbiddenFieldsException;
import org.springframework.lang.Nullable;

import java.util.List;

public interface GiftCertificateService {
    void save(GiftCertificate certificate, List<Tag> tags);

    GiftCertificate get(String certificateName);

    List<GiftCertificate> getByTagName(String tagName);

    List<GiftCertificate> getByPartName(String partName);

    void update(GiftCertificate certificate, String[] fields, @Nullable List<Tag> tags)
            throws UpdatingForbiddenFieldsException;

    void delete(GiftCertificate certificate);
}
