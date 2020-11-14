package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateWithTagsDto;
import com.epam.esm.entity.GiftCertificate;
import org.springframework.lang.Nullable;

import java.util.List;

public interface GiftCertificateService {
    GiftCertificate save(GiftCertificateDto dto);

    List<GiftCertificate> getAll();

    List<GiftCertificateWithTagsDto> search(@Nullable String tagName, @Nullable String partOfNameOrDesc,
                                            boolean nameSort, boolean dateSort);

    GiftCertificate getById(long id);

    GiftCertificate search(String certificateName);

    List<GiftCertificate> getByTagName(String tagName);

    List<GiftCertificate> searchByPartNameOrDescription(String partName);

    GiftCertificate update(GiftCertificateDto dto);

    void delete(GiftCertificate certificate);
}
