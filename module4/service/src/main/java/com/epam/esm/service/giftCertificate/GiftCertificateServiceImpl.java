package com.epam.esm.service.giftCertificate;

import com.epam.esm.repository.GiftCertificateMapper;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.dto.GiftCertificateDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateRepository giftCertificateRepository;
    private final TagRepository tagRepository;
    private final GiftCertificateMapper mapper;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateRepository giftCertificateRepository,
                                      TagRepository tagRepository,
                                      GiftCertificateMapper mapper) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagRepository = tagRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public GiftCertificate save(GiftCertificate certificate) {
        saveTags(certificate.getTags());
        return giftCertificateRepository.save(certificate);
    }

    private void saveTags(Set<Tag> tags) {
        saveUnregisterTags(tags);
        getRegisterTagIds(tags);
    }

    private void saveUnregisterTags(Set<Tag> tags) {
        tags.stream()
                .filter(tag -> !tagRepository.existsTagByName(tag.getName()))
                .forEach(tag -> tag = tagRepository.save(tag));
    }

    private void getRegisterTagIds(Set<Tag> tags) {
        tags.stream()
                .filter(tag -> tag.getId() == 0)
                .forEach(tag -> tagRepository.findByName(tag.getName()).ifPresent(result -> tag.setId(result.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GiftCertificate> findAll(Pageable pageable) {
        return giftCertificateRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GiftCertificate> search(@Nullable Set<String> tagNames, @Nullable String partOfNameOrDesc,
                                        @Nullable Pageable pageable) {
        return collect(pageable, tagNames, partOfNameOrDesc);
    }

    private boolean isPartNameOrDescriptionPassed(String param) {
        return param != null && param.trim().length() > 0;
    }

    private boolean isTagNamesPassed(Set<String> tagNames) {
        return tagNames != null && tagNames.size() > 0;
    }

    private Page<GiftCertificate> collect(Pageable pageable, Set<String> tagNames, String partOfName) {
        return isTagNamesPassed(tagNames) ?
                collectByTagAndPartNameOrDescription(pageable, tagNames, partOfName)
                : collectByPartNameOrDescriptionOnly(pageable, partOfName);
    }

    private Page<GiftCertificate> collectByTagAndPartNameOrDescription
            (Pageable pageable, Set<String> tagNames, String partOfName) {
        return isPartNameOrDescriptionPassed(partOfName) ?
                collectByBothParameters(pageable, tagNames, partOfName) :
                collectByTagNameOnly(pageable, tagNames);
    }

    private Page<GiftCertificate> collectByBothParameters(Pageable pageable,
                                                          Set<String> tagNames,
                                                          String partNameOrDescription) {
        return giftCertificateRepository.findByTagAndPartNameOrDescription(tagNames, tagNames.size(),
                partNameOrDescription, pageable);
    }

    private Page<GiftCertificate> collectByTagNameOnly(Pageable pageable, Set<String> tagNames) {
        return giftCertificateRepository.findByTagName(tagNames, tagNames.size(), pageable);
    }

    private Page<GiftCertificate> collectByPartNameOrDescriptionOnly(Pageable pageable,
                                                                     String partNameOrDescription) {
        return giftCertificateRepository.findDistinctByNameLikeOrDescriptionLike(partNameOrDescription, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GiftCertificate> findById(int id) {
        return giftCertificateRepository.findById(id);
    }

    @Override
    @Transactional
    public GiftCertificate update(GiftCertificateDto dto) {
        saveTagsIfPassed(dto);
        GiftCertificate certificate = giftCertificateRepository.getOne(dto.getId());
        checkDuration(dto, certificate);
        mapper.updateGiftCertificateFromDto(dto, certificate);
        return giftCertificateRepository.save(certificate);
    }

    private void saveTagsIfPassed(GiftCertificateDto dto) {
        if (dto.getTags() != null && dto.getTags().size() > 0) {
            saveTags(dto.getTags());
        } else {
            dto.setTags(null);
        }
    }

    private void checkDuration(GiftCertificateDto dto, GiftCertificate certificate) {
        if (dto.getDuration() == 0) {
            dto.setDuration(certificate.getDuration());
        }
    }

    @Override
    @Transactional
    public void delete(GiftCertificate certificate) {
        giftCertificateRepository.delete(certificate);
    }
}
