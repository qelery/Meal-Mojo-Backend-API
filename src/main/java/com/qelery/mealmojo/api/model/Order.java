package com.qelery.mealmojo.api.model;

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
@Getter @Setter @ToString
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
    private PaymentMethod paymentMethod;

    @Column
    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @OneToMany(mappedBy="order")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OrderLine> orderLines;

}
