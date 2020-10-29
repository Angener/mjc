package com.epam.esm.controller;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ExceptionDetail;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping(value = "/tags")
    public List<Tag> getAll() {
        return tagService.getAll();
    }

    @GetMapping(value = "/tags/{name}")
    public Tag get(@PathVariable String name) throws LocalizedControllerException {
        try {
            return tagService.get(name);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException(ExceptionDetail.TAG_NOT_FOUND_EXCEPTION);
        }
    }

    @PostMapping(value = "/tags")
    public void save(@RequestBody Tag tag) throws LocalizedControllerException {
        try {
            tagService.save(tag);
        } catch (DuplicateKeyException ex) {
            throw new LocalizedControllerException(ExceptionDetail.DUPLICATE_KEY);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException(ExceptionDetail.TAG_DOES_NOT_CONTAIN_NAME);
        }
    }

    @DeleteMapping(value = "/tags")
    public void delete(@RequestBody Tag tag) {
        tagService.delete(tag);
    }
}
