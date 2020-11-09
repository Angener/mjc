package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateWithTagsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.UpdatingForbiddenFieldsException;
import org.springframework.lang.Nullable;

import java.util.List;

public interface GiftCertificateService {
    long save(GiftCertificateDto dto);

    List<GiftCertificate> getAll();

    List<GiftCertificateWithTagsDto> search(@Nullable String tagName, @Nullable String partOfNameOrDesc,
                                            boolean nameSort, boolean dateSort);

    GiftCertificate getById(long id);

    GiftCertificate search(String certificateName);

    List<GiftCertificate> getByTagName(String tagName);

    List<GiftCertificate> searchByPartNameOrDescription(String partName);

    void update(GiftCertificateDto dto) throws UpdatingForbiddenFieldsException;

    void delete(GiftCertificate certificate);
}
