package com.epam.esm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MostWidelyUsedTag implements Serializable {
    private static final long serialVersionUID = 1L;
    private Tag motWidelyUsedTag;
    private BigDecimal highestOrderCost;
}
