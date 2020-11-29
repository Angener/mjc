package com.epam.esm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SqlResultSetMapping;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"id", "giftCertificates"})
@SqlResultSetMapping(name = "mostWidelyUsedTagMapper",
        classes = {
                @ConstructorResult(targetClass = MostWidelyUsedTag.class,
                        columns = {
                                @ColumnResult(name = "tag_id", type = long.class),
                                @ColumnResult(name = "tag_name", type = String.class),
                                @ColumnResult(name = "highest_cost", type = BigDecimal.class)
                        })})
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true)
    @NonNull
    private String name;
    @ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<GiftCertificate> giftCertificates;

    public Tag(int id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }
}
