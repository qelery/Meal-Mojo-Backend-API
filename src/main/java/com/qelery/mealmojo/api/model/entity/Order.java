package com.qelery.mealmojo.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private OffsetDateTime dateTime = OffsetDateTime.now();
    private Double tip;
    private Boolean isCompleted = false;
    private Boolean isDelivery;

    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy="order")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OrderLine> orderLines;

    @ManyToOne
    @JoinColumn(name="customer_profile_id", referencedColumnName="id")
    private CustomerProfile customerProfile;

    @ManyToOne
    @JoinColumn(name="restaurant_id", referencedColumnName="id")
    private Restaurant restaurant;
}
