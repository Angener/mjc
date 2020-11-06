package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.UpdatingForbiddenFieldsException;

import java.util.List;

public interface GiftCertificateService {
    void save(GiftCertificateDto dto);

    List<GiftCertificate> getAll();

    GiftCertificate getById(long id);

    GiftCertificate get(String certificateName);

    List<GiftCertificate> getByTagName(String tagName);

    List<GiftCertificate> searchByPartNameOrDescription(String partName);

    void update(GiftCertificateDto dto) throws UpdatingForbiddenFieldsException;

    void delete(GiftCertificate certificate);
}
