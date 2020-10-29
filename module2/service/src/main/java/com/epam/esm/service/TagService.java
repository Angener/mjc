package com.epam.esm.service;

import com.epam.esm.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAll ();
    Tag get(String name);
    void save(Tag tag);
    void delete(Tag tag);
}
