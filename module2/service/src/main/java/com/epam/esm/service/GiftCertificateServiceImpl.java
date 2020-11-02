package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.UpdatingForbiddenFieldsException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftCertificateServiceImpl implements GiftCertificateService {

    @Autowired GiftCertificateDao giftCertificateDao;
    @Autowired TagService tagService;

    @Override
    @Transactional
    public void save(GiftCertificate certificate, List<Tag> tags){
        saveTags(tags);
        saveGiftCertificate(certificate, getTagsWithTagIdFromDatabase(tags));
    }

    private void saveTags(List<Tag> tags){
        tags.forEach(tagService::save);
    }

    private List<Tag> getTagsWithTagIdFromDatabase(List<Tag> tags){
        return tags.stream()
                .map(tag -> tagService.get(tag.getName()))
                .collect(Collectors.toList());
    }

    private void saveGiftCertificate(GiftCertificate certificate, List<Tag> tags){
        giftCertificateDao.save(certificate, tags);
    }

    @Override
    @Transactional
    public void update(GiftCertificate certificate, String[] fields, @Nullable List<Tag> tags)
            throws UpdatingForbiddenFieldsException {
        checkUpdatableFields(fields);
        saveTagsIfRequired(tags);
        updateGiftCertificate(certificate, fields, tags);
    }

    private void checkUpdatableFields(String[] fields) throws UpdatingForbiddenFieldsException {
        if (isUpdatableRequestContainsForbiddenFields(fields)){
            throw new UpdatingForbiddenFieldsException();
        }
    }

    private boolean isUpdatableRequestContainsForbiddenFields(String[] fields){
        return Arrays.stream(fields)
                .anyMatch(field -> field.trim().toLowerCase().equals("createdate") ||
                        field.trim().toLowerCase().equals("lastupdatedate"));
    }

    private void saveTagsIfRequired(List<Tag> tags){
        if (isRequestOfGiftCertificatesContainsTags(tags)){
            saveTags(tags);
        }
    }

    private boolean isRequestOfGiftCertificatesContainsTags(List<Tag> tags){
        return tags.size() > 0;
    }

    private void updateGiftCertificate(GiftCertificate certificate, String[] fields, @Nullable List<Tag> tags){
        giftCertificateDao.update(certificate, fields, tags);
    }

    @Override
    public GiftCertificate get(String certificateName) {
        return giftCertificateDao.get(certificateName);
    }

    @Override
    public List<GiftCertificate> getByTagName(String tagName) {
        return giftCertificateDao.getByTagName(tagName);
    }

    @Override
    public List<GiftCertificate> getByPartName(String partName) {
        return giftCertificateDao.getByPartName(partName);
    }

    @Override
    public void delete(GiftCertificate certificate) {
        giftCertificateDao.delete(certificate);
    }
}
