package com.epam.esm.service;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAll() {
        return tagDao.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAll(int page, int recordsPerPage) {
        return recordsPerPage > 0 ? getPaginateTags(page, recordsPerPage) : getAll();
    }

    private List<Tag> getPaginateTags(int page, int recordsPerPage) {
        int startPosition = getStartPosition(page, recordsPerPage);
        int recordsQuantity = getRecordsQuantity(startPosition, recordsPerPage);
        return checkResultTagList(tagDao.getAll(startPosition, recordsQuantity));
    }

    private int getStartPosition(int page, int recordsPerPage) {
        return (page == 0) ? (0) : (page * recordsPerPage);
    }

    private int getRecordsQuantity(int startPosition, int recordsPerPage) {
        int menuSize = (int) tagDao.getTagsQuantity() - startPosition;
        int recordsQuantity = Math.min(recordsPerPage, menuSize);
        Preconditions.checkArgument(recordsQuantity > 0);
        return recordsQuantity;
    }

    private List<Tag> checkResultTagList(List<Tag> tags) {
        Preconditions.checkArgument(tags.size() > 0);
        return tags;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAllGiftCertificateTags(GiftCertificate giftCertificate) {
        return tagDao.getAllGiftCertificateTags(giftCertificate);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getById(int id) {
        return tagDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag get(String name) {
        return tagDao.getByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag save(Tag tag) {
        return tagDao.save(tag);
    }

    @Override
    @Transactional
    public void delete(Tag tag) {
        tagDao.delete(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTagsQuantity(){
        return tagDao.getTagsQuantity();
    }
}
