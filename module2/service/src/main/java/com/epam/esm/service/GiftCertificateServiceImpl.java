package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.UpdatingForbiddenFieldsException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GiftCertificateServiceImpl implements GiftCertificateService {
    GiftCertificateDao giftCertificateDao;

    @Override
    @Transactional
    public void save(GiftCertificateDto dto) {
        GiftCertificate certificate = dto.getGiftCertificate();
        List<Tag> tags = dto.getTags();
        giftCertificateDao.save(certificate, tags);
    }

    @Override
    @Transactional
    public void update(GiftCertificateDto dto) throws UpdatingForbiddenFieldsException {
        checkUpdatableFields(dto.getFields());
        updateGiftCertificate(dto.getGiftCertificate(), dto.getFields(), dto.getTags());
    }

    private void checkUpdatableFields(String[] fields) throws UpdatingForbiddenFieldsException {
        if (isUpdatableRequestContainsForbiddenFields(fields)) {
            throw new UpdatingForbiddenFieldsException();
        }
    }

    private boolean isUpdatableRequestContainsForbiddenFields(String[] fields) {
        return Arrays.stream(fields)
                .anyMatch(field -> field.trim().toLowerCase().equals("createdate") ||
                        field.trim().toLowerCase().equals("lastupdatedate"));
    }

    private void updateGiftCertificate(GiftCertificate certificate, String[] fields, @Nullable List<Tag> tags) {
        giftCertificateDao.update(certificate, fields, tags);
    }

    @Override
    public List<GiftCertificate> getAll() {
        return giftCertificateDao.getAll();
    }

    @Override
    public GiftCertificate getById(long id){
        return giftCertificateDao.getById(id);
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
    public List<GiftCertificate> searchByPartNameOrDescription(String partName) {
        return giftCertificateDao.searchByPartNameOrDescription(partName);
    }

    @Override
    public void delete(GiftCertificate certificate) {
        giftCertificateDao.delete(certificate);
    }
}
