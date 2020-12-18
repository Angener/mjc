package com.epam.esm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class MostWidelyUsedTag extends RepresentationModel<MostWidelyUsedTag> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Tag mostWidelyUsedTag;
    private final BigDecimal highestCost;

    public MostWidelyUsedTag(int id, String name, BigDecimal highestOrderCost) {
        mostWidelyUsedTag = new Tag(id, name);
        this.highestCost = highestOrderCost;
    }
}
