package com.epam.esm.controller;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TagController extends RepresentationModel<TagController> {
    private static final int DEFAULT_RECORDS_PER_PAGE = 10;
    private final TagService tagService;
    private final GiftCertificateService giftCertificateService;

    @Autowired
    public TagController(TagService tagService, GiftCertificateService giftCertificateService) {
        this.tagService = tagService;
        this.giftCertificateService = giftCertificateService;
    }

    @GetMapping(value = "/tags")
    public CollectionModel<Tag> getAll(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                       @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
        try {
            return getTagCollectionModel(page, size);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException("exception.message.40401", 40401, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    private CollectionModel<Tag> getTagCollectionModel(int page, int size) {
        List<Tag> tags = tagService.getAll(page, size);
        tags.forEach(tag -> linkTag(tag, size));
        CollectionModel<Tag> model = CollectionModel.of(tags, linkTo(methodOn(TagController.class)
                .getAll(page, size)).withSelfRel());
        paginateModel(model, page, size);
        return model;
    }

    private void linkTag(Tag tag, int size) {
        linkTagWithItself(tag);
        linkTagWithCertificates(tag, size);
    }

    private void linkTagWithItself(Tag tag) {
        tag.add(linkTo(methodOn(TagController.class).get(tag.getId())).withSelfRel());
    }

    private void linkTagWithCertificates(Tag tag, int size) {
        if (isCertificatesContainTag(tag)) {
            tag.add(linkTo(methodOn(GiftCertificateController.class).getCertificates(Collections.singleton(tag.getName()),
                    "", new ArrayList<>(), 0, size)).withRel("TagGiftCertificates"));
        }
    }

    private boolean isCertificatesContainTag(Tag tag) {
        return giftCertificateService.getByTagName(Collections.singleton(tag.getName())).size() > 0;
    }

    private void paginateModel(CollectionModel<Tag> model, int page, int size) {
        if (size > 0) {
            paginate(model, page, size);
        }
    }

    private void paginate(CollectionModel<Tag> model, int page, int size) {
        linkToPreviousPages(model, page, size);
        linkToNextPages(model, page, size);
    }

    private void linkToPreviousPages(CollectionModel<Tag> model, int page, int size) {
        if (page > 0) {
            int previousPage = page - 1;
            model.add(linkTo(methodOn(TagController.class).getAll(0, size)).withRel("firstPage"));
            model.add(linkTo(methodOn(TagController.class).getAll(previousPage, size)).withRel("previousPage"));
        }
    }

    private void linkToNextPages(CollectionModel<Tag> model, int page, int size) {
        long tagsQuantity = tagService.getTagsQuantity();
        if (isNextPagesExist(tagsQuantity, page, size)) {
            int lastPage = findLastPageNumber(tagsQuantity, size);
            model.add(linkTo(methodOn(TagController.class).getAll(page + 1, size)).withRel("nextPage"));
            model.add(linkTo(methodOn(TagController.class).getAll(lastPage, size)).withRel("lastPage"));
        }
    }

    private boolean isNextPagesExist(long tagsQuantity, int page, int size) {
        return tagsQuantity > (page + 1) * size;
    }

    private int findLastPageNumber(long tagsQuantity, int size) {
        int lastPage = (int) (tagsQuantity + size - 1) / size;
        return tagsQuantity % lastPage == 0 ? lastPage - 1 : lastPage;
    }

    @GetMapping("/tags/{id}")
    public Tag get(@PathVariable int id) {
        try {
            return getLinkedTag(id);
        } catch (NullPointerException ex) {
            throw new LocalizedControllerException("exception.message.40401", 40401, HttpStatus.NOT_FOUND);
        }
    }

    private Tag getLinkedTag(int id) {
        Tag tag = tagService.getById(id);
        linkTag(tag, DEFAULT_RECORDS_PER_PAGE);
        return tag;
    }

    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public Tag save(@RequestBody Tag tag) {
        try {
            return tagService.save(tag);
        } catch (PersistenceException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException("exception.message.40001", 40001, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/tags")
    public void delete(@RequestBody Tag tag) {
        tagService.delete(tag);
    }
}
