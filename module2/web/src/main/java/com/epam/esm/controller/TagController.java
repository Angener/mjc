package com.epam.esm.controller;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ExceptionDetail;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.service.TagService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TagController {
    TagService service;

    @GetMapping(value = "/tags")
    public List<Tag> getAll() {
        return service.getAll();
    }

    @GetMapping("/tags/{name}")
    public Tag get(@PathVariable String name) throws LocalizedControllerException {
        try {
            return service.get(name);
        } catch (EmptyResultDataAccessException ex) {
            throw new LocalizedControllerException(ExceptionDetail.TAG_NOT_FOUND_EXCEPTION);
        }
    }

    @PostMapping("/tags")
    public void save(@RequestBody Tag tag) throws LocalizedControllerException {
        try {
            service.save(tag);
        } catch (DataIntegrityViolationException ex) {
            throw new LocalizedControllerException(ExceptionDetail.TAG_DOES_NOT_CONTAIN_NAME);
        }
    }

    @DeleteMapping("/tags")
    public void delete(@RequestBody Tag tag) {
        service.delete(tag);
    }
}
