package com.epam.esm.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "gift_certificate")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiftCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String description;
    BigDecimal price;
    ZonedDateTime createDate;
    ZonedDateTime lastUpdateDate;
    int duration;

    public GiftCertificate(long id, String name, String description, BigDecimal price,
                           ZonedDateTime createDate, ZonedDateTime lastUpdateDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.duration = duration;
    }
}
