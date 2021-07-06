package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Entity
@Table(name="order")
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private OffsetDateTime dateTime = OffsetDateTime.now();
    private Double tip;
    private Boolean completed = false;
    private Boolean delivery;

    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private PaymentMethod paymentMethod;

    @OneToMany(mappedBy="order")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OrderLine> orderLines;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="customer_profile_id", referencedColumnName="id")
    private CustomerProfile customerProfile;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id", referencedColumnName="id")
    private Restaurant restaurant;
}
