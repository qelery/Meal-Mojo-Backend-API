package com.qelery.mealmojo.api.model;

import com.qelery.mealmojo.api.model.enums.DeliveryMethod;
import com.qelery.mealmojo.api.model.enums.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter @NoArgsConstructor
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

    public Order(LocalDateTime dateTime, Double tip, Boolean complete, PaymentMethod paymentMethod,
                 DeliveryMethod deliveryMethod, List<OrderLine> orderLines) {
        this.dateTime = dateTime;
        this.tip = tip;
        this.complete = complete;
        this.paymentMethod = paymentMethod;
        this.deliveryMethod = deliveryMethod;
        this.orderLines = orderLines;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", tip=" + tip +
                ", complete=" + complete +
                ", paymentMethod=" + paymentMethod +
                ", deliveryMethod=" + deliveryMethod +
                ", orderLines=" + orderLines +
                '}';
    }
}
