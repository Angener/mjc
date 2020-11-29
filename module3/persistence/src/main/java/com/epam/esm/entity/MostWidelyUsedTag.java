package com.epam.esm.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MostWidelyUsedTag implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Tag mostWidelyUsedTag;
    private final BigDecimal highestCost;

    public MostWidelyUsedTag(long id, String name, BigDecimal highestOrderCost) {
        mostWidelyUsedTag = new Tag(id, name);
        this.highestCost = highestOrderCost;
    }
}
