package com.epam.esm.model.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

@Component
public class GiftCertificateModelAssembler extends RepresentationModelAssemblerSupport<GiftCertificate, GiftCertificateModel> {

    public GiftCertificateModelAssembler() {
        super(GiftCertificateController.class, GiftCertificateModel.class);
    }

    @Override
    @NonNull
    public GiftCertificateModel toModel(@NonNull GiftCertificate certificate) {
        GiftCertificateModel certificateModel = instantiateModel(certificate);

        certificateModel.add(linkTo(
                methodOn(GiftCertificateController.class)
                        .getById(certificate.getId()))
                .withSelfRel());

        certificateModel.setId(certificate.getId());
        certificateModel.setName(certificate.getName());
        certificateModel.setDescription(certificate.getDescription());
        certificateModel.setPrice(certificate.getPrice());
        certificateModel.setCreateDate(certificate.getCreateDate());
        certificateModel.setLastUpdateDate(certificate.getLastUpdateDate());
        certificateModel.setDuration(certificate.getDuration());
        certificateModel.setTags(toTagModel(certificate.getTags()));
        return certificateModel;
    }

    @Override
    @NonNull
    public CollectionModel<GiftCertificateModel> toCollectionModel(
            @NonNull Iterable<? extends GiftCertificate> entities) {
        CollectionModel<GiftCertificateModel> certificateModels = super.toCollectionModel(entities);
        certificateModels.add(linkTo(methodOn(GiftCertificateController.class)
                .getAll(PageRequest.of(0, 0))).withSelfRel());
        return certificateModels;
    }

    private Set<TagModel> toTagModel(Set<Tag> tags) {
        return tags.isEmpty() ? Collections.emptySet() : getTagModel(tags);
    }

    private Set<TagModel> getTagModel(Set<Tag> tags) {
        return tags.stream()
                .map(tag -> TagModel.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build()
                        .add(linkTo(
                                methodOn(TagController.class)
                                        .findById(tag.getId()))
                                .withSelfRel()))
                .collect(Collectors.toSet());
    }
}
