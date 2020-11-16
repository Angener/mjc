package com.epam.esm.service;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public List<Tag> getAll() {
        return tagDao.getAll();
    }

    @Override
    public List<Tag> getAllGiftCertificateTags(GiftCertificate giftCertificate) {
        return tagDao.getAllGiftCertificateTags(giftCertificate);
    }

    @Override
    public Tag getById(long id) {
        return tagDao.getById(id);
    }

    @Override
    public Tag get(String name) {
        return tagDao.getByName(name);
    }

    @Override
    public Tag save(Tag tag) {
        return tagDao.save(tag);
    }

    @Override
    public void delete(Tag tag) {
        tagDao.delete(tag);
    }
}
