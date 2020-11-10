package com.epam.esm.service;

import com.epam.esm.dao.TagDaoImpl;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;

public class TagServiceImplTest {
    @InjectMocks @Spy TagServiceImpl service;
    @Mock TagDaoImpl dao;
    static List<Tag> tags;

    @BeforeAll
    public static void init() {
        tags = Arrays.asList(new Tag(1, "first"), new Tag(2, "second"));
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAll() {
        when(dao.getAll()).thenReturn(tags);

        assertEquals(tags, service.getAll());
        verify(dao).getAll();
        verify(service).getAll();
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getAllGiftCertificateTags() {
        when(dao.getAllGiftCertificateTags(any(GiftCertificate.class))).thenReturn(tags);

        assertEquals(tags, service.getAllGiftCertificateTags(new GiftCertificate()));
        verify(dao).getAllGiftCertificateTags(new GiftCertificate());
        verify(service).getAllGiftCertificateTags(new GiftCertificate());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getById() {
        when(dao.getById(anyLong())).thenReturn(tags.get(0));

        assertEquals(tags.get(0), service.getById(anyLong()));
        verify(dao).getById(anyLong());
        verify(service).getById(anyLong());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getByName() {
        when(dao.getByName(anyString())).thenReturn(tags.get(0));

        assertEquals(tags.get(0), service.get(anyString()));
        verify(dao).getByName(anyString());
        verify(service).get(anyString());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void save() {
        when(dao.save(any(Tag.class))).thenReturn(1L);

        assertEquals(1, service.save(new Tag()));
        verify(dao).save(new Tag());
        verify(service).save(new Tag());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void delete() {
        service.delete(new Tag());
        verify(dao).delete(new Tag());
        verify(service).delete(new Tag());
        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(dao);
    }
}
