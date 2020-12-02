package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAll();

    List<Tag> getAll(int page, int recordsPerPage);

    List<Tag> getAllGiftCertificateTags(GiftCertificate giftCertificate);

    Tag getById(int id);

    Tag get(String name);

    Tag save(Tag tag);

    void delete(Tag tag);

    long getTagsQuantity();
}
