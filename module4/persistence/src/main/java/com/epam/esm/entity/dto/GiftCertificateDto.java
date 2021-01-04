package com.epam.esm.entity.dto;

import com.epam.esm.entity.Tag;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class GiftCertificateDto implements Serializable {
    static final long serialVersionUID = 1L;
    int id;
    String name;
    String description;
    BigDecimal price;
    int duration;
    Set<Tag> tags;
}
