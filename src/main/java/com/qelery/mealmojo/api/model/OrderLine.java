package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qelery.mealmojo.api.model.enums.PurchaseStatus;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString(exclude={"order", "cart", "menuItem", "restaurant"})
@NoArgsConstructor @AllArgsConstructor
public class OrderLine {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer quantity;
    private Double priceEach;

    @Column
    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="menu_item_id")
    private MenuItem menuItem;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;
}
