package com.epam.esm.model;

import com.epam.esm.entity.User;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "orders", itemRelation = "order")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderModel extends RepresentationModel<OrderModel> {
    private int id;
    private ZonedDateTime orderDate;
    private BigDecimal orderCost;
    private User user;
    private GiftCertificateModel certificate;
}
