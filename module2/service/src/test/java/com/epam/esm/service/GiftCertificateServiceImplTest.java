package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDaoImpl;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateWithTagsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class GiftCertificateServiceImplTest {
    static final String TAG_NAME = "tagName";
    static final String PART_NAME = "partName";
    static Set<Tag> tags;
    static List<String> tagNames;
    static List<GiftCertificate> certificates;
    static GiftCertificate certificate;
    Map<String, Object> fields;
    GiftCertificateDto dto;
    @Mock
    GiftCertificateDaoImpl dao;
    @Mock
    TagServiceImpl tagService;
    @InjectMocks
    GiftCertificateServiceImpl service;

    @BeforeAll
    public static void init() {
        initTags();
        initTagNames();
    }

    private static void initTags(){
        tags = new HashSet<>();
        tags.add(new Tag(1, "first"));
        tags.add(new Tag(2, "second"));
    }

    private static void initTagNames(){
        tagNames = tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    @BeforeEach
    public void setUp() {
        setCertificate();
        setFields();
        setDto();
        setCertificates();
    }

    private void setCertificate(){
        certificate = new GiftCertificate();
        certificate.setId(1L);
        certificate.setName("name");
        certificate.setDescription("description");
    }

    private void setFields(){
        fields = new HashMap<>();
        fields.put("id", certificate.getId());
    }

    private void setDto(){
        dto = new GiftCertificateDto(certificate, tags);
    }

    private void setCertificates(){
        certificates = Collections.singletonList(certificate);
    }

    @Test
    public void save() {
        when(dao.save(dto.getGiftCertificate(), dto.getTags())).thenReturn(certificate);

        assertEquals(certificate, service.save(dto));
        verify(dao).save(dto.getGiftCertificate(), dto.getTags());
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void update() {
        fields.put("name", certificate.getName());
        fields.put("description", certificate.getDescription());

        when(dao.update(anyMap(), anySet())).thenReturn(certificate);

        assertEquals(service.update(dto), certificate);
        verify(dao).update(fields, tags);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void updateAllCertificateFields() {
        certificate.setPrice(new BigDecimal("21"));
        certificate.setDuration(12);
        fields.put("name", certificate.getName());
        fields.put("description", certificate.getDescription());
        fields.put("price", certificate.getPrice());
        fields.put("duration", certificate.getDuration());

        when(dao.update(anyMap(), anySet())).thenReturn(certificate);

        assertEquals(service.update(dto), certificate);
        verify(dao).update(fields, tags);
    }

    @Test
    public void updateNeverUpdatingDateFields() {
        certificate.setName(null);
        certificate.setDescription(null);
        certificate.setCreateDate(ZonedDateTime.now());
        certificate.setLastUpdateDate(ZonedDateTime.now());
        fields.put("createDate", certificate.getCreateDate());
        fields.put("lastUpdateDate", certificate.getLastUpdateDate());

        when(dao.update(anyMap(), anySet())).thenReturn(certificate);

        service.update(dto);

        verify(dao, times(0)).update(fields, tags);
    }

    @Test
    public void searchByTagNameAndPartName() {
        when(dao.searchByTagAndPartNameOrDescription(tagNames, TAG_NAME, PART_NAME)).thenReturn(certificates);
        when(tagService.getAllGiftCertificateTags(any(GiftCertificate.class))).thenReturn(new ArrayList<>(tags));

        assertEquals(Collections.singletonList(new GiftCertificateWithTagsDto(certificate, new ArrayList<>(tags))),
                service.search(TAG_NAME, PART_NAME, tagNames));
        verify(dao).searchByTagAndPartNameOrDescription(tagNames, TAG_NAME, PART_NAME);
        verify(tagService).getAllGiftCertificateTags(certificate);
    }

    @Test
    public void searchByTagOnly() {
        when(dao.getByTagName(tagNames, TAG_NAME)).thenReturn(certificates);
        when(tagService.getAllGiftCertificateTags(any(GiftCertificate.class))).thenReturn(new ArrayList<>(tags));

        assertEquals(Collections.singletonList(new GiftCertificateWithTagsDto(certificate, new ArrayList<>(tags))),
                service.search(TAG_NAME, null, tagNames));
        verify(dao).getByTagName(tagNames, TAG_NAME);
        verify(tagService).getAllGiftCertificateTags(certificate);
    }

    @Test
    public void searchByPartNameOnly() {
        when(dao.searchByPartNameOrDescription(tagNames, PART_NAME)).thenReturn(certificates);
        when(tagService.getAllGiftCertificateTags(any(GiftCertificate.class))).thenReturn(new ArrayList<>(tags));
        service.search(null, PART_NAME, tagNames);

        verify(dao).searchByPartNameOrDescription(tagNames, PART_NAME);
        verify(tagService).getAllGiftCertificateTags(certificate);
    }

    @Test
    public void getAll() {
        when(dao.getAll()).thenReturn(certificates);

        assertEquals(certificates, service.getAll());
        verify(dao).getAll();
    }

    @Test
    public void getById() {
        when(dao.getById(anyLong())).thenReturn(certificates.get(0));

        assertEquals(certificates.get(0), service.getById(anyLong()));
        verify(dao).getById(anyLong());
    }

    @Test
    public void getByName() {
        when(dao.getByName(anyString())).thenReturn(certificates.get(0));

        assertEquals(certificates.get(0), service.search(anyString()));
        verify(dao).getByName(anyString());
    }

    @Test
    public void getByTagName() {
        when(dao.getByTagName(null, TAG_NAME)).thenReturn(certificates);

        assertEquals(certificates, service.getByTagName(TAG_NAME));
        verify(dao).getByTagName(null, TAG_NAME);
    }

    @Test
    public void searchByPartNameOrDescription() {
        when(dao.searchByPartNameOrDescription(null, PART_NAME)).thenReturn(certificates);

        assertEquals(certificates, service.searchByPartNameOrDescription(PART_NAME));
        verify(dao).searchByPartNameOrDescription(null, PART_NAME);
    }

    @Test
    public void delete() {
        service.delete(certificate);
        verify(dao).delete(certificate);
    }
}
