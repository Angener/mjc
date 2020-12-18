package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TagService {
    Page<Tag> findAll(Pageable pageable);

    Optional<Tag> findById(int id);

    Tag save(Tag tag);

    void delete(Tag tag);
}
