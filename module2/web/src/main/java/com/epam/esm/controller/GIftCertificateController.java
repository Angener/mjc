package com.epam.esm.controller;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.ExceptionDetail;
import com.epam.esm.exception.LocalizedControllerException;
import com.epam.esm.exception.UpdatingForbiddenFieldsException;
import com.epam.esm.service.GiftCertificateService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GIftCertificateController {

    @Autowired GiftCertificateService service;
    @Autowired ObjectMapper mapper;

    @PostMapping("/giftCertificates")
    public void save(@RequestBody ObjectNode json) throws LocalizedControllerException {
        try {
            service.save(buildGiftCertificationFromJsonTree(json), convertJsonValueToTagList(json));
        } catch (IOException ex) {
            throw new LocalizedControllerException(ExceptionDetail.INTERNAL_SERVER_ERROR);
        }
    }

    private GiftCertificate buildGiftCertificationFromJsonTree(ObjectNode json) throws IOException {
        return mapper.treeToValue(json.get("giftCertificate"), GiftCertificate.class);
    }

    private List<Tag> convertJsonValueToTagList(ObjectNode json) {
        return mapper.convertValue(json.findValue("tags"), new TypeReference<List<Tag>>() {
        });
    }

    @PatchMapping("/giftCertificate")
    public void update(@RequestBody ObjectNode json) throws LocalizedControllerException {
        try {
            service.update(buildGiftCertificationFromJsonTree(json),
                    convertJsonValueToFieldsArray(json),
                    convertJsonValueToTagList(json));
        }catch (UpdatingForbiddenFieldsException ex){
            throw new LocalizedControllerException(ExceptionDetail.UPDATING_FORBIDDEN_DATE_FIELDS);
        } catch (IOException ex){
            throw new LocalizedControllerException(ExceptionDetail.INTERNAL_SERVER_ERROR);
        }
    }

    private String[] convertJsonValueToFieldsArray(ObjectNode json) {
        return mapper.convertValue(json.findValue("fields"), new TypeReference<String[]>() {
        });
    }

    //TODO this method is refactoring now.
    // It will be able returns certificates by tag, part name or description and sort them.
    @GetMapping("/giftCertificates/{name}")
    public GiftCertificate get(@PathVariable String name) {
        return service.get(name);
    }
}
