package com.epam.esm.service;

import com.epam.esm.dao.TagDaoImpl;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {
    private static List<Tag> tags;

    @Mock
    private TagDaoImpl dao;

    @InjectMocks
    private TagServiceImpl service;

    @BeforeAll
    public static void init() {
        tags = Arrays.asList(new Tag(1, "first"), new Tag(2, "second"));
    }

    @Test
    public void getAll() {
        when(dao.getAll()).thenReturn(tags);

        assertEquals(service.getAll(), tags);
        verify(dao).getAll();
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getAllGiftCertificateTags() {
        when(dao.getAllGiftCertificateTags(any(GiftCertificate.class))).thenReturn(tags);

        assertEquals(tags, service.getAllGiftCertificateTags(new GiftCertificate()));
        verify(dao).getAllGiftCertificateTags(new GiftCertificate());
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getById() {
        when(dao.getById(anyInt())).thenReturn(tags.get(0));

        assertEquals(tags.get(0), service.getById(anyInt()));
        verify(dao).getById(anyInt());
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getByName() {
        when(dao.getByName(anyString())).thenReturn(tags.get(0));

        assertEquals(tags.get(0), service.get(anyString()));
        verify(dao).getByName(anyString());
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void save() {
        when(dao.save(any(Tag.class))).thenReturn(any(Tag.class));
        service.save(new Tag());

        verify(dao).save(new Tag());
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void delete() {
        service.delete(new Tag());

        verify(dao).delete(new Tag());
        verifyNoMoreInteractions(dao);
    }
}
