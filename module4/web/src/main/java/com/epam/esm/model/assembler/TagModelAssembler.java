package com.epam.esm.model.assembler;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.controller.TagController;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.model.GiftCertificateModel;
import com.epam.esm.model.TagModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagModelAssembler extends RepresentationModelAssemblerSupport<Tag, TagModel> {
    public TagModelAssembler() {
        super(TagController.class, TagModel.class);
    }

    @Override
    @NonNull
    public TagModel toModel(@NonNull Tag tag) {
        TagModel tagModel = instantiateModel(tag);

        tagModel.add(linkTo(
                methodOn(TagController.class)
                        .findById(tag.getId()))
                .withSelfRel());

        tagModel.setId(tag.getId());
        tagModel.setName(tag.getName());
        tagModel.setGiftCertificates(toGiftCertificateModel(tag.getGiftCertificates()));

        return tagModel;
    }

    @Override
    @NonNull
    public CollectionModel<TagModel> toCollectionModel(
            @NonNull Iterable<? extends Tag> entities) {
        CollectionModel<TagModel> tagModels = super.toCollectionModel(entities);
        tagModels.add(linkTo(methodOn(TagController.class)
                .findAll(PageRequest.of(0, 0))).withSelfRel());
        return tagModels;
    }

    private Set<GiftCertificateModel> toGiftCertificateModel(Set<GiftCertificate> certificates) {
        return certificates.isEmpty() ? Collections.emptySet() : getGiftCertificateModel(certificates);
    }

    private Set<GiftCertificateModel> getGiftCertificateModel(Set<GiftCertificate> certificates) {
        return certificates.stream()
                .map(certificate -> GiftCertificateModel.builder()
                        .id(certificate.getId())
                        .name(certificate.getName())
                        .duration(certificate.getDuration())
                        .build()
                        .add(linkTo(
                                methodOn(GiftCertificateController.class)
                                        .getById(certificate.getId()))
                                .withSelfRel()))
                .collect(Collectors.toSet());
    }
}
