package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDaoImpl;
import com.epam.esm.dao.SortCertificatesType;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.UpdatingForbiddenFieldsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GiftCertificateServiceImplTest {
    @InjectMocks
    @Spy
    GiftCertificateServiceImpl service;
    @Mock
    GiftCertificateDaoImpl dao;
    @Mock
    GiftCertificate certificate;
    @Mock
    GiftCertificateDto dto;
    static Set<Tag> tags;
    static List<GiftCertificate> certificates;
    SortCertificatesType type;

    @BeforeAll
    public static void init() {
        tags = new HashSet<>();
        tags.add(new Tag(1, "first"));
        tags.add(new Tag(2, "second"));
        certificates = Collections.singletonList(new GiftCertificate());
    }

    @BeforeEach
    public void setUp() {
        type = SortCertificatesType.NONE;
        MockitoAnnotations.openMocks(this);
        dto.setGiftCertificate(certificate);
        dto.setTags(tags);
    }

    @Test
    public void save() {
        when(dao.save(dto.getGiftCertificate(), dto.getTags())).thenReturn(1L);

        assertEquals(1, service.save(dto));
        verify(dao).save(dto.getGiftCertificate(), dto.getTags());
        verifyNoMoreInteractions(dao);
        verify(service).save(dto);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void update() throws UpdatingForbiddenFieldsException {
        when(dto.getFields()).thenReturn(new String[]{"name"});
        service.update(dto);

        verify(service).update(dto);
        verify(dao).update(dto.getGiftCertificate(), dto.getFields(), dto.getTags());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void updateThrowsUpdatingForbiddenException() {
        when(dto.getFields()).thenReturn(new String[]{"createDate"});

        assertThrows(UpdatingForbiddenFieldsException.class, () -> service.update(dto));
        when(dto.getFields()).thenReturn(new String[]{"lastUpdateDate"});

        assertThrows(UpdatingForbiddenFieldsException.class, () -> service.update(dto));
        verifyNoInteractions(dao);
    }

    @Test
    public void searchByTagNameAndPartName() {
        String tagName = "first";
        String partName = "th";
        service.search(tagName, partName, false, false);
        when(service.getSortType(false, false)).thenReturn(type);
        InOrder inOrder = inOrder(service, dao);

        inOrder.verify(service).search(tagName, partName, false, false);
        inOrder.verify(service).getSortType(false, false);
        inOrder.verify(service).collectByBothParameters(type, tagName, partName);
        inOrder.verify(dao).searchByTagAndPartNameOrDescription(type, tagName, partName);
        inOrder.verify(service, times(0)).collectByTagNameOnly(type, tagName);
        inOrder.verify(service, times(0)).collectByPartNameOrDescriptionOnly(type, partName);
        inOrder.verify(service, times(1)).collectCertificates(anyList());
        inOrder.verify(service).collectAllCertificatesTags(anyList());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void searchByTagOnly() {
        String tagName = "first";
        String partName = "";
        service.search(tagName, partName, false, false);
        when(service.getSortType(false, false)).thenReturn(type);
        InOrder inOrder = inOrder(service, dao);

        inOrder.verify(service).search(tagName, partName, false, false);
        inOrder.verify(service).getSortType(false, false);
        inOrder.verify(service, times(0)).collectByBothParameters(type, tagName, partName);
        inOrder.verify(service).collectByTagNameOnly(type, tagName);
        inOrder.verify(dao).getByTagName(type, tagName);
        inOrder.verify(service, times(0)).collectByPartNameOrDescriptionOnly(type, partName);
        inOrder.verify(service, times(1)).collectCertificates(anyList());
        inOrder.verify(service).collectAllCertificatesTags(anyList());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void searchByPartNameOnly() {
        String tagName = "";
        String partName = "wh";
        service.search(tagName, partName, false, false);
        when(service.getSortType(false, false)).thenReturn(type);
        InOrder inOrder = inOrder(service, dao);

        inOrder.verify(service).search(tagName, partName, false, false);
        inOrder.verify(service).getSortType(false, false);
        inOrder.verify(service, times(0)).collectByBothParameters(type, tagName, partName);
        inOrder.verify(service, times(0)).collectByTagNameOnly(type, tagName);
        inOrder.verify(service).collectByPartNameOrDescriptionOnly(type, partName);
        inOrder.verify(dao).searchByPartNameOrDescription(type, partName);
        inOrder.verify(service).collectCertificates(anyList());
        inOrder.verify(service).collectAllCertificatesTags(anyList());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAll() {
        when(dao.getAll()).thenReturn(certificates);

        assertEquals(certificates, service.getAll());
        verify(dao).getAll();
        verify(service).getAll();
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getById() {
        when(dao.getById(anyLong())).thenReturn(certificates.get(0));

        assertEquals(certificates.get(0), service.getById(anyLong()));
        verify(dao).getById(anyLong());
        verify(service).getById(anyLong());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getByName() {
        when(dao.getByName(anyString())).thenReturn(certificates.get(0));

        assertEquals(certificates.get(0), service.search(anyString()));
        verify(dao).getByName(anyString());
        verify(service).search(anyString());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getByTagName() {
        when(dao.getByTagName(any(), anyString())).thenReturn(certificates);

        assertEquals(certificates, service.getByTagName(anyString()));
        verify(dao).getByTagName(any(), anyString());
        verify(service).getByTagName(anyString());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void searchByPartNameOrDescription() {
        when(dao.searchByPartNameOrDescription(any(), anyString())).thenReturn(certificates);

        assertEquals(certificates, service.searchByPartNameOrDescription(anyString()));
        verify(dao).searchByPartNameOrDescription(any(), anyString());
        verify(service).searchByPartNameOrDescription(anyString());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void delete() {
        service.delete(certificate);

        verify(dao).delete(certificate);
        verify(service).delete(certificate);
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void defineSortType(){
        assertEquals(SortCertificatesType.DATE_AND_NAME_SORT, service.getSortType(true, true));
        assertEquals(SortCertificatesType.NAME_SORT, service.getSortType(true, false));
        assertEquals(SortCertificatesType.DATE_SORT, service.getSortType(false, true));
        assertEquals(SortCertificatesType.NONE, service.getSortType(false, false));
    }
}
