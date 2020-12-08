package com.epam.esm.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "gift_certificate")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = "tags", callSuper = false)
public class GiftCertificate extends RepresentationModel<GiftCertificate> implements Serializable {
    static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(nullable = false, unique = true)
    String name;
    String description;
    @Column(columnDefinition = "MONEY")
    BigDecimal price;
    @Column(name = "create_date", columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false)
    ZonedDateTime createDate;
    @Column(name = "last_update_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    ZonedDateTime lastUpdateDate;
    int duration;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "tag_gift_certificate",
            joinColumns = {@JoinColumn(name = "gift_certificate_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    Set<Tag> tags;

    public GiftCertificate(int id, String name, String description, BigDecimal price,
                           ZonedDateTime createDate, ZonedDateTime lastUpdateDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.duration = duration;
    }

    @PrePersist
    private void setDates() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        setCreateDate(zonedDateTime);
        setLastUpdateDate(zonedDateTime);
    }

    @PreUpdate
    private void setUpdatableDate() {
        setLastUpdateDate(ZonedDateTime.now());
    }
}
