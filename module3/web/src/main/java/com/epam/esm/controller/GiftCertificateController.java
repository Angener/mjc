package com.epam.esm.controller;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.CollectionModel;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class GiftCertificateController {

    private final GiftCertificateService giftCertificateService;
    private final TagService tagService;

    @Autowired
    public GiftCertificateController(GiftCertificateService service, TagService tagService) {
        this.giftCertificateService = service;
        this.tagService = tagService;
    }

    @PostMapping("/giftCertificates")
    @ResponseStatus(HttpStatus.CREATED)
    public GiftCertificate save(@RequestBody GiftCertificate certificate) {
        try {
            return giftCertificateService.save(certificate);
        } catch (DuplicateKeyException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException("exception.message.40003", 40003, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/giftCertificates")
    public GiftCertificate update(@RequestBody GiftCertificate certificate) {
        try {
            return giftCertificateService.update(certificate);
        } catch (DuplicateKeyException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/giftCertificates")
    public CollectionModel<GiftCertificate> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
        try {
            return getGiftCertificateCollectionModel(giftCertificateService.getAll(page, size), page, size);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException("exception.message.40402", 40402, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    private CollectionModel<GiftCertificate> getGiftCertificateCollectionModel(List<GiftCertificate> certificates,
                                                                               int page, int size) {
        certificates.forEach(this::linkCertificate);
        CollectionModel<GiftCertificate> model = CollectionModel
                .of(certificates, linkTo(methodOn(GiftCertificateController.class)
                        .getAll(page, size)).withSelfRel());
        paginateModel(model, page, size);
        return model;
    }

    private void linkCertificate(GiftCertificate certificate) {
        linkCertificateWithItself(certificate);
        linkCertificateWithTags(certificate);
    }

    private void linkCertificateWithItself(GiftCertificate certificate) {
        certificate.add(linkTo(methodOn(GiftCertificateController.class).getById(certificate.getId())).withSelfRel());
    }

    private void linkCertificateWithTags(GiftCertificate certificate) {
        if (isTagsContainCertificate(certificate)) {
            certificate.getTags().stream()
                    .filter(tag -> tag.getLinks().isEmpty())
                    .forEach(tag -> tag.add(linkTo(methodOn(TagController.class).get(tag.getId())).withSelfRel()));
        }
    }

    private boolean isTagsContainCertificate(GiftCertificate certificate) {
        return tagService.getAllGiftCertificateTags(certificate).size() > 0;
    }

    private void paginateModel(CollectionModel<GiftCertificate> model, int page, int size) {
        if (size > 0) {
            paginate(model, page, size);
        }
    }

    private void paginate(CollectionModel<GiftCertificate> model, int page, int size) {
        linkToPreviousPages(model, page, size);
        linkToNextPages(model, page, size);
    }

    private void linkToPreviousPages(CollectionModel<GiftCertificate> model, int page, int size) {
        if (page > 0) {
            int previousPage = page - 1;
            model.add(linkTo(methodOn(GiftCertificateController.class).getAll(0, size)).withRel("firstPage"));
            model.add(linkTo(methodOn(GiftCertificateController.class).getAll(previousPage, size))
                    .withRel("previousPage"));
        }
    }

    private void linkToNextPages(CollectionModel<GiftCertificate> model, int page, int size) {
        long tagsQuantity = giftCertificateService.getGiftCertificatesQuantity();
        if (isNextPagesExist(tagsQuantity, page, size)) {
            int lastPage = findLastPageNumber(tagsQuantity, size);
            model.add(linkTo(methodOn(GiftCertificateController.class).getAll(page + 1, size)).withRel("nextPage"));
            model.add(linkTo(methodOn(GiftCertificateController.class).getAll(lastPage, size)).withRel("lastPage"));
        }
    }

    private boolean isNextPagesExist(long tagsQuantity, int page, int size) {
        return tagsQuantity > (page + 1) * size;
    }

    private int findLastPageNumber(long tagsQuantity, int size) {
        int lastPage = (int) (tagsQuantity + size - 1) / size - 1;
        return tagsQuantity % lastPage == 0 ? lastPage - 1 : lastPage;
    }

    @GetMapping("/giftCertificates/{id}")
    public GiftCertificate getById(@PathVariable int id) {
        try {
            return giftCertificateService.getById(id);
        } catch (NullPointerException ex) {
            throw new LocalizedControllerException("exception.message.40402", 40402, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/giftCertificates/search")
    public CollectionModel<GiftCertificate> getCertificates(
            @RequestParam(value = "tagNames", required = false) Set<String> tagNames,
        @RequestParam(value = "partNameOrDesc", required = false, defaultValue = "") String partNameOrDesc,
        @RequestParam(value = "sortTypes", required = false) List<String> sortTypes,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "0") int size) {


        List<GiftCertificate> certificates = giftCertificateService.search(tagNames, partNameOrDesc, sortTypes);


        if (tagNames == null){
            tagNames = new HashSet<>();
        }

        if (sortTypes == null){
            sortTypes = new ArrayList<>();
        }


        try {
            //tags quantity
            int tagsQuantity = certificates.size();

            certificates.forEach(this::linkCertificate);

            CollectionModel<GiftCertificate> model;

            //paginate certificate list
            if (size > 0) {
                certificates = giftCertificateService.getPaginatedCertificateList(certificates, page, size);


                 model = CollectionModel
                        .of(certificates, linkTo(methodOn(GiftCertificateController.class)
                                .getCertificates(tagNames, partNameOrDesc, sortTypes, page, size)).withSelfRel());


                if (page > 0){
                    int previousPage = page - 1;
                    model.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(
                            tagNames, partNameOrDesc, sortTypes, 0, size)).withRel("firstPage"));
                    model.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(
                            tagNames, partNameOrDesc, sortTypes, previousPage, size)).withRel("previousPage"));
                }
                // link to next pages
                if (tagsQuantity > (page + 1) * size){
                    // finding last page
                    int lastPage = findLastPageNumber(tagsQuantity, size);
                    model.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(
                            tagNames, partNameOrDesc, sortTypes, page + 1, size)).withRel("nextPage"));
                    model.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(
                            tagNames, partNameOrDesc, sortTypes, lastPage, size)).withRel("lastPage"));
                }
            } else {
                model = CollectionModel
                        .of(certificates, linkTo(methodOn(GiftCertificateController.class)
                                .getCertificates(tagNames, partNameOrDesc, sortTypes, 0, 0)).withSelfRel());
            }

            return model;
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }























    @DeleteMapping("/giftCertificates")
    public void delete(@RequestBody GiftCertificate certificate) {
        giftCertificateService.delete(certificate);
    }
}












//    @GetMapping("/giftCertificates/search")
//    public CollectionModel<GiftCertificate> getCertificates(
//            @RequestParam(value = "tagNames", required = false) Set<String> tagNames,
//            @RequestParam(value = "partNameOrDesc", required = false, defaultValue = "") String partNameOrDesc,
//            @RequestParam(value = "sortTypes", required = false) List<String> sortTypes,
//            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
//            @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
//
//
//        List<GiftCertificate> certificates = giftCertificateService.search(tagNames, partNameOrDesc, sortTypes);
//
//        try {
//            return size > 0 ? getPaginatedCertificateModel(certificates, page, size) :
//                    getGiftCertificateCollectionModel(certificates, page, size);
//        } catch (IllegalArgumentException ex) {
//            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    private CollectionModel<GiftCertificate> getPaginatedCertificateModel(List<GiftCertificate> certificates,
//                                                                          int page, int size) {
//        certificates = giftCertificateService.getPaginatedCertificateList(certificates, page, size);
//        return getGiftCertificateCollectionModel(certificates, page, size);
//    }


