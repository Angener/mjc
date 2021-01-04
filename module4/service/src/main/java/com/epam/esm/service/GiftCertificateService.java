package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.dto.GiftCertificateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.Set;

public interface GiftCertificateService {
    GiftCertificate save(GiftCertificate certificate);

    Page<GiftCertificate> findAll(Pageable pageable);

    Page<GiftCertificate> search(@Nullable Set<String> tagNames, @Nullable String partOfNameOrDesc,
                                 @Nullable Pageable pageable);

    Optional<GiftCertificate> findById(int id);

    GiftCertificate update(GiftCertificateDto dto);

    void delete(GiftCertificate certificate);
}
