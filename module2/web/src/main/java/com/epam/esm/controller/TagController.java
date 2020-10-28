package com.epam.esm.controller;

import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@RequestMapping("/")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping(value = "/tags")
    public List<Tag> getAll(){
        return tagService.getAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/tags/{name}")
    public Tag get(@PathVariable String name){
        try{
           return tagService.get(name);
        } catch (NullPointerException | EmptyResultDataAccessException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/tags")
    public void save(@PathVariable String name){
        try{
            tagService.save(name);
        } catch (NullPointerException | DataIntegrityViolationException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag name doesn't ");
        }
    }

}
