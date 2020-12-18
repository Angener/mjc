package com.epam.esm.service;

import com.epam.esm.repository.GiftCertificateMapper;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.dto.GiftCertificateDto;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.giftCertificate.GiftCertificateServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class GiftCertificateServiceImplTest {
    static final String PART_NAME = "partName";
    static Set<Tag> tags;
    static Set<String> tagNames;
    static List<GiftCertificate> certificates;
    static GiftCertificate certificate;
    @Mock
    GiftCertificateRepository repository;
    @Mock
    TagRepository tagRepository;
    @Mock
    GiftCertificateMapper mapper;
    @InjectMocks
    GiftCertificateServiceImpl service;

    @BeforeAll
    public static void init() {
        initTags();
        initTagNames();
    }

    private static void initTags() {
        tags = new HashSet<>();
        tags.add(new Tag(1, "first"));
        tags.add(new Tag(2, "second"));
    }

    private static void initTagNames() {
        tagNames = tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }

    @BeforeEach
    public void setUp() {
        setCertificate();
        setCertificates();
    }

    private void setCertificate() {
        certificate = new GiftCertificate();
        certificate.setId(1);
        certificate.setName("name");
        certificate.setDescription("description");
        certificate.setTags(tags);
    }

    private void setCertificates() {
        certificates = Collections.singletonList(certificate);
    }

    @Test
    public void save() {
        when(repository.save(any())).thenReturn(certificate);

        assertEquals(certificate, service.save(certificate));
        verify(repository).save(certificate);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void updateWhenTagsWasPassed() {
        GiftCertificateDto dto = new GiftCertificateDto();
        dto.setTags(tags);
        when(repository.getOne(anyInt())).thenReturn(certificate);
        when(repository.save(any())).thenReturn(certificate);

        assertEquals(service.update(dto), certificate);
        verify(repository).getOne(anyInt());
        verify(tagRepository, times(2)).existsTagByName(anyString());
        verify(tagRepository, times(2)).save(any(Tag.class));
        verify(repository).save(certificate);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void updateWhenTagsWasNotPassed() {
        GiftCertificateDto dto = new GiftCertificateDto();
        when(repository.getOne(anyInt())).thenReturn(certificate);
        when(repository.save(any())).thenReturn(certificate);

        assertEquals(service.update(dto), certificate);
        verifyNoInteractions(tagRepository);
        verify(repository).getOne(anyInt());
        verify(repository).save(certificate);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void searchByTagNameAndPartName() {
        service.search(tagNames, PART_NAME, Pageable.unpaged());
        verify(repository).findByTagAndPartNameOrDescription(tagNames, tagNames.size(), PART_NAME, Pageable.unpaged());
    }

    @Test
    public void searchByTagOnly() {
        service.search(tagNames, null, Pageable.unpaged());
        verify(repository).findByTagName(tagNames, tagNames.size(), Pageable.unpaged());
    }

    @Test
    public void searchByPartNameOnly() {
        service.search(null, PART_NAME, Pageable.unpaged());
        verify(repository).findDistinctByNameLikeOrDescriptionLike(PART_NAME, Pageable.unpaged());
    }

    @Test
    public void findAll() {
        service.findAll(Pageable.unpaged());
        verify(repository).findAll(Pageable.unpaged());
    }

    @Test
    public void findById() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(certificates.get(0)));

        assertEquals(Optional.of(certificates.get(0)), service.findById(anyInt()));
        verify(repository).findById(anyInt());
    }

    @Test
    public void delete() {
        service.delete(certificate);
        verify(repository).delete(certificate);
    }
}
