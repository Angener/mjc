package com.epam.esm.controller;

import com.epam.esm.model.assembler.GiftCertificateModelAssembler;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.dto.GiftCertificateDto;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.model.GiftCertificateModel;
import com.epam.esm.service.GiftCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
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

import java.util.Set;

@RestController
public class GiftCertificateController {

    private final GiftCertificateService giftCertificateService;
    private final PagedResourcesAssembler<GiftCertificate> pagedResourcesAssembler;
    private final GiftCertificateModelAssembler giftCertificateModelAssembler;

    @Autowired
    public GiftCertificateController(GiftCertificateService service,
                                     PagedResourcesAssembler<GiftCertificate> pagedResourcesAssembler,
                                     GiftCertificateModelAssembler giftCertificateModelAssembler) {
        this.giftCertificateService = service;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.giftCertificateModelAssembler = giftCertificateModelAssembler;
    }

    @PostMapping("/giftCertificates")
    @ResponseStatus(HttpStatus.CREATED)
    public GiftCertificate save(@RequestBody GiftCertificate certificate) {
        try {
            return giftCertificateService.save(certificate);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        } catch (NullPointerException ex) {
            throw new LocalizedControllerException("exception.message.40003", 40003, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/giftCertificates")
    public GiftCertificate update(@RequestBody GiftCertificateDto dto) {
        try {
            return giftCertificateService.update(dto);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/giftCertificates")
    public PagedModel<GiftCertificateModel> getAll(Pageable pageable) {
        try {
            return pagedResourcesAssembler.toModel(giftCertificateService.findAll(pageable),
                    giftCertificateModelAssembler);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException("exception.message.40402", 40402, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/giftCertificates/{id}")
    public GiftCertificateModel getById(@PathVariable int id) {
        return giftCertificateModelAssembler.toModel(giftCertificateService.findById(id).orElseThrow(() ->
                new LocalizedControllerException("exception.message.40402", 40402, HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/giftCertificates/search")
    public PagedModel<GiftCertificateModel> getCertificates(
            @RequestParam(value = "tagNames", required = false) Set<String> tagNames,
            @RequestParam(value = "partNameOrDesc", required = false, defaultValue = "") String partNameOrDesc,
            Pageable pageable) {
        try {
            return pagedResourcesAssembler.toModel(giftCertificateService.search(tagNames, partNameOrDesc, pageable),
                    giftCertificateModelAssembler);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/giftCertificates")
    public void delete(@RequestBody GiftCertificate certificate) {
        giftCertificateService.delete(certificate);
    }
}
