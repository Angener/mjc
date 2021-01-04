package com.epam.esm.controller;

import com.epam.esm.model.assembler.TagModelAssembler;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.model.TagModel;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TagController extends RepresentationModel<TagController> {
    private final TagService tagService;
    private final PagedResourcesAssembler<Tag> pagedResourcesAssembler;
    private final TagModelAssembler tagModelAssembler;

    @Autowired
    public TagController(TagService tagService,
                         PagedResourcesAssembler<Tag> pagedResourcesAssembler,
                         TagModelAssembler tagModelAssembler) {
        this.tagService = tagService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.tagModelAssembler = tagModelAssembler;
    }

    @GetMapping(value = "/tags")
    public PagedModel<TagModel> findAll(Pageable pageable) {
        try {
            return pagedResourcesAssembler.toModel(tagService.findAll(pageable),
                    tagModelAssembler);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException("exception.message.40401", 40401, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/tags/{id}")
    public TagModel findById(@PathVariable int id) {
        return tagModelAssembler.toModel(tagService.findById(id).orElseThrow(() ->
                new LocalizedControllerException("exception.message.40401", 40401, HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public Tag save(@RequestBody Tag tag) {
        try {
            return tagService.save(tag);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/tags")
    @Secured("ROLE_ADMIN")
    public void delete(@RequestBody Tag tag) {
        tagService.delete(tag);
    }
}
