package com.epam.esm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "certificates", itemRelation = "certificate")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftCertificateModel extends RepresentationModel<GiftCertificateModel> {
    int id;
    String name;
    String description;
    BigDecimal price;
    ZonedDateTime createDate;
    ZonedDateTime lastUpdateDate;
    int duration;
    Set<TagModel> tags;
}
