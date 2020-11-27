package com.epam.esm.controller;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.GiftCertificateService;
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
public class GiftCertificateController {

    private final GiftCertificateService service;

    @Autowired
    public GiftCertificateController(GiftCertificateService service) {
        this.service = service;
    }

    @PostMapping("/giftCertificates")
    @ResponseStatus(HttpStatus.CREATED)
    public GiftCertificate save(@RequestBody GiftCertificate certificate) {
        try {
            return service.save(certificate);
        } catch (DuplicateKeyException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException("exception.message.40003", 40003, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/giftCertificates")
    public GiftCertificate update(@RequestBody GiftCertificate certificate) {
        try {
            System.err.println(certificate);
            return service.update(certificate);
        } catch (DuplicateKeyException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/giftCertificates")
    public List<GiftCertificate> getAll() {
        try {
            return service.getAll();
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException("exception.message.40402", 40402, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/giftCertificates/{id}")
    public GiftCertificate getById(@PathVariable long id) {
        try {
            return service.getById(id);
        } catch (NullPointerException ex) {
            throw new LocalizedControllerException("exception.message.40402", 40402, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/giftCertificates/search")
    public List<GiftCertificate>
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
