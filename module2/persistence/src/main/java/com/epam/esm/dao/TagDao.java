package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;

import java.util.List;

public interface TagDao {
    List<Tag> getAll();

    Tag getByName(String name);

    Tag getById(long id);

    List<Tag> getAllGiftCertificateTags(GiftCertificate giftCertificate);

    Tag save(Tag tag);

    void delete(Tag tag);
}
