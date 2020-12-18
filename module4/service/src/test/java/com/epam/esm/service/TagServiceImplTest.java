package com.epam.esm.service;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.tag.TagServiceImpl;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {
    private static List<Tag> tags;
    private static Page<Tag> tagPage;

    @Mock
    private TagRepository repository;

    @InjectMocks
    private TagServiceImpl service;

    @BeforeAll
    public static void init() {
        tags = Arrays.asList(new Tag(1, "first"), new Tag(2, "second"));
        tagPage = new PageImpl<>(tags);
    }

    @Test
    public void findAll() {
        when(repository.findAll(Pageable.unpaged())).thenReturn(tagPage);

        assertEquals(service.findAll(Pageable.unpaged()), tagPage);
        verify(repository).findAll(Pageable.unpaged());
    }

    @Test
    public void findById() {
        when(repository.findById(anyInt())).thenReturn(Optional.of(tags.get(0)));

        assertEquals(Optional.of(tags.get(0)), service.findById(anyInt()));
        verify(repository).findById(anyInt());
    }

    @Test
    public void save() {
        service.save(new Tag());
        verify(repository).save(new Tag());
    }

    @Test
    public void delete() {
        service.delete(new Tag());
        verify(repository).delete(new Tag());
    }
}
