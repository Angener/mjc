package com.epam.esm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "\"order\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends RepresentationModel<Tag> implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "create_date", columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false)
    private ZonedDateTime orderDate;
    @Column(name = "order_cost", columnDefinition = "MONEY", nullable = false, updatable = false)
    private BigDecimal orderCost;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToOne
    private GiftCertificate certificate;

    @PrePersist
    private void setUp() {
        setUpOrderDate();
        setUpOrderCost();
        orderDate = ZonedDateTime.now();
    }

    private void setUpOrderDate() {
        orderDate = ZonedDateTime.now();
    }

    private void setUpOrderCost() {
        orderCost = certificate.getPrice();
    }
}
