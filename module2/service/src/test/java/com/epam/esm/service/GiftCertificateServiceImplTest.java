package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDaoImpl;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateWithTagsDto;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GiftCertificateServiceImplTest {
    @InjectMocks @Spy GiftCertificateServiceImpl service;
    @Mock GiftCertificateDaoImpl dao;
    @Mock GiftCertificate certificate;
    @Mock GiftCertificateDto dto;
    static Set<Tag> tags;
    static List<GiftCertificate> certificates;
    Comparator<GiftCertificateWithTagsDto> dateComparator = Comparator.comparing(d ->
            d.getCertificate().getCreateDate());
    Comparator<GiftCertificateWithTagsDto> nameComparator = Comparator.comparing(d -> d.getCertificate().getName());


    @BeforeAll
    public static void init(){
        tags = new HashSet<>();
        tags.add(new Tag(1, "first"));
        tags.add(new Tag(2, "second"));
        certificates = Collections.singletonList(new GiftCertificate());
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dto.setGiftCertificate(certificate);
        dto.setTags(tags);
    }

    @Test
    public void save(){
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
    public void searchByTagNameAndParName(){
        String tagName = "first";
        String partName = "th";
        service.search(tagName, partName, false, false);
        InOrder inOrder = inOrder(service);

        inOrder.verify(service).search(tagName, partName, false, false);
        inOrder.verify(service).collectByBothParameters(tagName, partName);
        inOrder.verify(service, times(0)).collectByTagNameOnly(tagName);
        inOrder.verify(service, times(0)).collectByPartNameOrDescriptionOnly(partName);
        inOrder.verify(service, times(2)).collectCertificates(anyList());
        inOrder.verify(service).collectAllCertificatesTags(anyList());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void searchByTagOnly(){
        String tagName = "first";
        String partName = "";
        service.search(tagName, partName, false, false);
        InOrder inOrder = inOrder(service);

        inOrder.verify(service).search(tagName, partName, false, false);
        inOrder.verify(service, times(0)).collectByBothParameters(tagName, partName);
        inOrder.verify(service).collectByTagNameOnly(tagName);
        inOrder.verify(service, times(0)).collectByPartNameOrDescriptionOnly(partName);
        inOrder.verify(service, times(1)).collectCertificates(anyList());
        inOrder.verify(service).collectAllCertificatesTags(anyList());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void searchByPartNameOnly(){
        String tagName = "";
        String partName = "wh";
        service.search(tagName, partName, false, false);
        InOrder inOrder = inOrder(service);

        inOrder.verify(service).search(tagName, partName, false, false);
        inOrder.verify(service, times(0)).collectByBothParameters(tagName, partName);
        inOrder.verify(service, times(0)).collectByTagNameOnly(tagName);
        inOrder.verify(service).collectByPartNameOrDescriptionOnly(partName);
        inOrder.verify(service, times(1)).collectCertificates(anyList());
        inOrder.verify(service).collectAllCertificatesTags(anyList());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void searchWithTagDateComparing(){
        when(service.collectCertificates(anyList())).thenReturn(getListGiftCertificatesWithTagsDto());
        doNothing().when(service).collectAllCertificatesTags(anyList());
        List<GiftCertificateWithTagsDto> certificates = new ArrayList<>(getListGiftCertificatesWithTagsDto());
        certificates.sort(dateComparator);

        assertEquals(certificates, service.search("first", "", false, true));
    }

    @Test
    public void searchWithTagNameComparing(){
        when(service.collectCertificates(anyList())).thenReturn(getListGiftCertificatesWithTagsDto());
        doNothing().when(service).collectAllCertificatesTags(anyList());
        List<GiftCertificateWithTagsDto> certificates = new ArrayList<>(getListGiftCertificatesWithTagsDto());
        certificates.sort(nameComparator);

        assertEquals(certificates, service.search("first", "", true, false));
    }

    @Test
    public void searchWithDateAndTagNameComparing(){
        when(service.collectCertificates(anyList())).thenReturn(getListGiftCertificatesWithTagsDto());
        doNothing().when(service).collectAllCertificatesTags(anyList());
        List<GiftCertificateWithTagsDto> certificates = new ArrayList<>(getListGiftCertificatesWithTagsDto());
        certificates.sort(dateComparator.thenComparing(nameComparator));

        assertEquals(certificates, service.search("first", "", true, true));
    }

    private Set<GiftCertificateWithTagsDto> getListGiftCertificatesWithTagsDto(){
        Set<GiftCertificateWithTagsDto> certificates = new HashSet<>();

        GiftCertificate certificate1 = new GiftCertificate();
        certificate1.setName("c");
        certificate1.setCreateDate(ZonedDateTime.of(LocalDate.of(2018, 12, 12),
                LocalTime.of(12, 12), ZoneId.of("GMT+3")));

        GiftCertificate certificate2 = new GiftCertificate();
        certificate2.setName("a");
        certificate2.setCreateDate(ZonedDateTime.of(LocalDate.of(2020, 12, 12),
                LocalTime.of(12, 12), ZoneId.of("GMT+3")));

        GiftCertificate certificate3 = new GiftCertificate();
        certificate3.setName("d");
        certificate3.setCreateDate(ZonedDateTime.of(LocalDate.of(2019, 12, 12),
                LocalTime.of(12, 12), ZoneId.of("GMT+3")));

        GiftCertificate certificate4 = new GiftCertificate();
        certificate4.setName("b");
        certificate4.setCreateDate(ZonedDateTime.of(LocalDate.of(2019, 12, 12),
                LocalTime.of(12, 12), ZoneId.of("GMT+3")));


        certificates.add(new GiftCertificateWithTagsDto(certificate1));
        certificates.add(new GiftCertificateWithTagsDto(certificate2));
        certificates.add(new GiftCertificateWithTagsDto(certificate3));
        certificates.add(new GiftCertificateWithTagsDto(certificate4));

        return certificates;
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
        when(dao.getByTagName(anyString())).thenReturn(certificates);

        assertEquals(certificates, service.getByTagName(anyString()));
        verify(dao).getByTagName(anyString());
        verify(service).getByTagName(anyString());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void searchByPartNameOrDescription() {
        when(dao.searchByPartNameOrDescription(anyString())).thenReturn(certificates);

        assertEquals(certificates, service.searchByPartNameOrDescription(anyString()));
        verify(dao).searchByPartNameOrDescription(anyString());
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
}
