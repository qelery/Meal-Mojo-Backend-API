package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qelery.mealmojo.api.model.enums.DeliveryMethod;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter @ToString(exclude={"user", "restaurant"})
@NoArgsConstructor @AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime dateTime;
    private Double tip;
    private Boolean complete;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private PaymentMethod paymentMethod;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private DeliveryMethod deliveryMethod;

    @OneToMany(mappedBy="order")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OrderLine> orderLines;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;
}
