package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qelery.mealmojo.api.model.enums.PurchaseStatus;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name="order_line")
public class OrderLine {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;
    private Double priceEach;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus;

    @ManyToOne
    @JoinColumn(name="menu_item_id", referencedColumnName="id")
    private MenuItem menuItem;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="order_id", referencedColumnName="id")
    private Order order;
}
