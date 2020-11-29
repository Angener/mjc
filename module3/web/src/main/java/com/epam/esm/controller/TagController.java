package com.epam.esm.controller;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.List;

@RestController
public class TagController {
    private final TagService service;

    @Autowired
    public TagController(TagService service) {
        this.service = service;
    }

    @GetMapping(value = "/tags")
    public List<Tag> getAll(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                            @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
        try {
            return service.getAll(page, size);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException("exception.message.40401", 40401, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new LocalizedControllerException("exception.message.40004", 40004, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/tags/{id}")
    public Tag get(@PathVariable int id) {
        try {
            return service.getById(id);
        } catch (NullPointerException ex) {
            throw new LocalizedControllerException("exception.message.40401", 40401, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public Tag save(@RequestBody Tag tag) {
        try {
            return service.save(tag);
        } catch (PersistenceException ex) {
            throw new LocalizedControllerException("exception.message.40002", 40002, HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException("exception.message.40001", 40001, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/tags")
    public void delete(@RequestBody Tag tag) {
        service.delete(tag);
    }
}
