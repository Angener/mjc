package com.epam.esm.controller;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateWithTagsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.ExceptionDetail;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.GiftCertificateService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiftCertificateController {

    GiftCertificateService service;

    @Autowired
    public GiftCertificateController(GiftCertificateService service) {
        this.service = service;
    }

    @PostMapping("/giftCertificates")
    @ResponseStatus(HttpStatus.CREATED)
    public GiftCertificate save(@RequestBody GiftCertificateDto dto) {
        try {
            return service.save(dto);
        } catch (DuplicateKeyException ex) {
            throw new LocalizedControllerException(ExceptionDetail.NAME_IS_NOT_UNIQUE);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException(ExceptionDetail.CERTIFICATE_TAGS_IS_NOT_AVAILABLE);
        }
    }

    @PatchMapping("/giftCertificates")
    public GiftCertificate update(@RequestBody GiftCertificateDto dto) {
        try {
            return service.update(dto);
        } catch (DuplicateKeyException ex) {
            throw new LocalizedControllerException(ExceptionDetail.NAME_IS_NOT_UNIQUE);
        }
    }

    @GetMapping("/giftCertificates")
    public List<GiftCertificate> getAll() {
        try {
            return service.getAll();
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException(ExceptionDetail.GIFT_CERTIFICATE_NOT_FOUND);
        }
    }

    @GetMapping("/giftCertificates/{id}")
    public GiftCertificate getById(@PathVariable long id) {
        try {
            return service.getById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException(ExceptionDetail.GIFT_CERTIFICATE_NOT_FOUND);
        }
    }

    @GetMapping("/giftCertificates/search")
    public List<GiftCertificateWithTagsDto>
    get(@RequestParam(required = false, defaultValue = "") String tagName,
        @RequestParam(required = false, defaultValue = "") String partNameOrDesc,
        @RequestParam(required = false) List<String> sortTypes) {
        return service.search(tagName, partNameOrDesc, sortTypes);
    }

    @DeleteMapping("/giftCertificates")
    public void delete(@RequestBody GiftCertificate certificate) {
        service.delete(certificate);
    }
}
