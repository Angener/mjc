package com.epam.esm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "tags", itemRelation = "tag")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagModel extends RepresentationModel<TagModel> {
    private int id;
    private String name;
    private Set<GiftCertificateModel> giftCertificates;
}
